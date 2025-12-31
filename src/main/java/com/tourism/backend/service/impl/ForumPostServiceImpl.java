package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.CommentRequestDTO;
import com.tourism.backend.dto.request.CreatePostRequestDTO;
import com.tourism.backend.dto.request.PostFilterRequest;
import com.tourism.backend.dto.response.*;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.ContentStatus;
import com.tourism.backend.enums.NotificationType;
import com.tourism.backend.enums.PostType;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.ForumPostService;
import com.tourism.backend.service.NotificationService;
import com.tourism.backend.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ForumPostServiceImpl implements ForumPostService {

    private final ForumPostRepository forumPostRepository;
    private final UserRepository userRepository;
    private final PostCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostCommentRepository postCommentRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;
    private final FollowerRepository followerRepository;

    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostListResponseDTO> getPosts(PostFilterRequest filter, Pageable pageable) {
        Specification<ForumPost> spec = Specification.where(null);

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), ContentStatus.PUBLISHED));

        if (filter.getCategoryId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("categoryID"), filter.getCategoryId()));
        }

        if (filter.getTagId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("postTags").get("tag").get("tagID"), filter.getTagId()));
        }

        if (filter.getPostType() != null && !filter.getPostType().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("postType"), filter.getPostType()));
        }

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), searchPattern),
                            cb.like(cb.lower(root.get("content")), searchPattern)
                    ));
        }

        Page<ForumPost> posts = forumPostRepository.findAll(spec, pageable);
        return posts.map(this::mapToPostListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostListResponseDTO> getTrendingPosts(Pageable pageable) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        Page<ForumPost> posts = forumPostRepository.findTrendingPosts(weekAgo, pageable);
        return posts.map(this::mapToPostListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostListResponseDTO> getPostsByUser(Integer userId, Pageable pageable) {
        Page<ForumPost> posts = forumPostRepository.findByUserUserIDAndStatus(
                userId, ContentStatus.PUBLISHED, pageable);
        return posts.map(this::mapToPostListResponse);
    }

    @Override
    @Transactional
    public PostDetailResponseDTO createPost(CreatePostRequestDTO request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        PostCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        ForumPost post = ForumPost.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .summary(request.getSummary())
                .content(request.getContent())
                .postType(PostType.valueOf(request.getPostType()))
                .status(ContentStatus.PUBLISHED)
                .publishedAt(LocalDateTime.now())
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .bookmarkCount(0)
                .shareCount(0)
                .isPinned(false)
                .isFeatured(false)
                .build();

        post = forumPostRepository.save(post);

        List<PostTag> postTags = new ArrayList<>();

        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            for (String tagName : request.getTagNames()) {
                Tag tag = tagRepository.findByTagNameIgnoreCase(tagName.trim())
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTagName(tagName.trim());
                            newTag.setSlug(SlugUtils.makeSlug(tagName.trim()));
                            newTag.setIsActive(true);
                            return tagRepository.save(newTag);
                        });

                PostTag postTag = new PostTag();
                postTag.setPost(post);
                postTag.setTag(tag);
                postTag = postTagRepository.save(postTag);

                postTags.add(postTag);
            }
        }

        post.setPostTags(postTags);
        notifyFollowersAboutNewPost(post, user);

        return mapToDetailResponse(post, username);
    }

    private void notifyFollowersAboutNewPost(ForumPost post, User author) {
        try {
            List<Integer> followerIds = followerRepository.findFollowerUserIdsByUserId(author.getUserID());

            if(followerIds.isEmpty()){
                log.info("No followers to notify for post {}", post.getPostID());
                return;
            }

            String title = "Bài viết mới";
            String message = String.format("%s vừa đăng bài viết mới: '%s'",
                    author.getFullName(),
                    post.getTitle());
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", post.getPostID());
            metadata.put("postTitle", post.getTitle());
            metadata.put("authorId", author.getUserID());
            metadata.put("authorName", author.getFullName());
            metadata.put("authorAvatar", author.getAvatar());
            metadata.put("postThumbnail", post.getThumbnailUrl());

            for(Integer followerId : followerIds){
                try {
                    notificationService.createNotification(
                            followerId,
                            NotificationType.NEW_POST_FROM_FOLLOWING,
                            title,
                            message,
                            metadata
                    );
                } catch (Exception e){
                    log.error("Failed to send notification to followers {}", followerId);
                }
            }
            log.info("Sent new post notifications to {} followers", followerIds.size());
        }catch (Exception e){
            log.error("Failed to notify followers about new post: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PostDetailResponseDTO getPostDetail(Integer postId, String username, String clientIp) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại"));

        // View tracking
        String viewKey = "post:view:" + postId + ":" + (username != null ? username : clientIp);
        Boolean hasViewed = redisTemplate.hasKey(viewKey);

        if (Boolean.FALSE.equals(hasViewed)) {
            post.setViewCount(post.getViewCount() + 1);
            forumPostRepository.save(post);
            redisTemplate.opsForValue().set(viewKey, "1", Duration.ofMinutes(30));
        }

        // Check like and bookmark status
        boolean isLiked = false;
        boolean isBookmarked = false;

        if (username != null) {
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                Integer userId = user.getUserID();
                isLiked = post.getLikes() != null && post.getLikes().stream()
                        .anyMatch(like -> like.getUser().getUserID().equals(userId));
                // Add bookmark check if you have bookmarks
            }
        }
        return mapToDetailResponse(post, isLiked, isBookmarked);
    }

    @Override
    @Transactional
    public void togglePostLike(Integer postId, String username) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại"));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<PostLike> existingLike = post.getLikes().stream()
                .filter(like -> like.getUser().getUserID().equals(user.getUserID()))
                .findFirst();

        if (existingLike.isPresent()) {
            post.getLikes().remove(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            log.info("User {} unliked post {}", user.getUserID(), postId);
        } else {
            PostLike newLike = new PostLike();
            newLike.setPost(post);
            newLike.setUser(user);
            post.getLikes().add(newLike);
            post.setLikeCount(post.getLikeCount() + 1);

            log.info("User {} liked post {}", user.getUserID(), postId);
            if (!post.getUser().getUserID().equals(user.getUserID())) {
                notifyPostLike(post, user);
            }
        }

        forumPostRepository.save(post);
    }

    private void notifyPostLike(ForumPost post, User liker) {
        try {
            String title = "Thích bài viết";
            String message = String.format("%s đã thích bài viết '%s' của bạn", liker.getFullName(), post.getTitle());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", post.getPostID());
            metadata.put("likerId", liker.getUserID());
            metadata.put("likerName", liker.getFullName());
            metadata.put("likerAvatar", liker.getAvatar());
            metadata.put("postTitle", post.getTitle());

            notificationService.createNotification(
                    post.getUser().getUserID(),
                    NotificationType.POST_LIKE,
                    title,
                    message,
                    metadata
            );
            log.info("Post like notification sent to user {}", post.getUser().getUserID());
        } catch (Exception e){
            log.error("Failed to sent post like notification: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PostDetailResponseDTO addComment(Integer postId, CommentRequestDTO request, String username) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại"));

        User commenter = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(commenter);
        comment.setContent(request.getContent().trim());
        comment.setStatus(ContentStatus.PUBLISHED);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setIsEdited(false);

        // Handle reply
        if (request.getParentCommentId() != null) {
            PostComment parentComment = postCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bình luận cha không tồn tại"));

            //GỬI THÔNG BÁO CHO NGƯỜI BỊ REPLY (nếu không phải tự reply chính mình)
            if (!parentComment.getPost().getPostID().equals(postId)) {
                throw new BadRequestException("Không thể trả lời bình luận của bài viết khác");
            }

            comment.setParentComment(parentComment);
            comment = postCommentRepository.save(comment);
            if(!parentComment.getUser().getUserID().equals(commenter.getUserID())){
                notifyCommentReply(parentComment, commenter, post, comment);
            }
        } else {
            comment = postCommentRepository.save(comment);
            //GỬI THÔNG BÁO CHO TÁC GIẢ BÀI VIẾT (nếu không phải tự comment vào bài của mình)
            if(!post.getUser().getUserID().equals(commenter.getUserID())){
                notifyPostComment(post, commenter, comment);
            }
        }
        post.setCommentCount(post.getCommentCount() + 1);
        forumPostRepository.save(post);

        return getPostDetail(postId, username, null);
    }

    private void notifyPostComment(ForumPost post, User commenter, PostComment comment) {
        try {
            String title = "Bình luận mới";
            String message = String.format("%s đã bình luận vào bài viết '%s' của bạn",
                    commenter.getFullName(),
                    post.getTitle());
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", post.getPostID());
            metadata.put("commentId", comment.getCommentID());
            metadata.put("commenterId", commenter.getUserID());
            metadata.put("commenterName", commenter.getFullName());
            metadata.put("commenterAvatar", commenter.getAvatar());
            metadata.put("postTitle", post.getTitle());
            notificationService.createNotification(
                    post.getUser().getUserID(),
                    NotificationType.NEW_COMMENT,
                    title,
                    message,
                    metadata
            );
            log.info("Notification send to comment author (userId = {})", post.getUser().getUserID());
        } catch (Exception e){
            log.error("Failed to send comment notification" , e);
        }
    }

    private void notifyCommentReply(PostComment parentComment, User replier, ForumPost post, PostComment replyComment) {
        try{
            String title = "Phản hồi mới";
            String message = String.format("%s đã phản hồi bình luận của bạn trong bài '%s'",
                    replier.getFullName(),
                    post.getTitle());
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", post.getPostID());
            metadata.put("commentId", replyComment.getCommentID());
            metadata.put("parentCommentId", parentComment.getCommentID());
            metadata.put("replierId", replier.getUserID());
            metadata.put("replierName", replier.getFullName());
            metadata.put("replierAvatar", replier.getAvatar());
            metadata.put("postTitle", post.getTitle());

            notificationService.createNotification(
                    parentComment.getUser().getUserID(),
                    NotificationType.COMMENT_REPLY,
                    title,
                    message,
                    metadata
            );
            log.info("Notification sent to comment author (userId = {})", parentComment.getUser().getUserID());
        }catch (Exception e){
            log.error("Failed to send reply notification", e);
        }
    }


    @Override
    @Transactional
    public void toggleCommentLike(Integer commentId, String username) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận không tồn tại"));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<CommentLike> existingLike = comment.getLikes().stream()
                .filter(like -> like.getUser().getUserID().equals(user.getUserID()))
                .findFirst();

        if (existingLike.isPresent()) {
            comment.getLikes().remove(existingLike.get());
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            log.info("User {} unliked comment {}", user.getUserID(), commentId);
        } else {
            CommentLike newLike = new CommentLike();
            newLike.setComment(comment);
            newLike.setUser(user);
            comment.getLikes().add(newLike);
            comment.setLikeCount(comment.getLikeCount() + 1);

            log.info("User {} liked comment {}", user.getUserID(), commentId);
            
            if(!comment.getUser().getUserID().equals(user.getUserID())){
                notifyCommentLike(comment, user);
            }
        }

        postCommentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPostLikeStatus(Integer postId, String username) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại"));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        boolean isLiked = post.getLikes() != null && post.getLikes().stream()
                .anyMatch(like -> like.getUser().getUserID().equals(user.getUserID()));

        return isLiked;
    }

    private void notifyCommentLike(PostComment comment, User liker) {
        try {
            ForumPost post = comment.getPost();
            String title = "Thích bình luận";
            String message = String.format("%s đã thích bình luận của bạn trong bài '%s'",
                    liker.getFullName(),
                    post.getTitle()
            );

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", post.getPostID());
            metadata.put("commentId", comment.getCommentID());
            metadata.put("likerId", liker.getUserID());
            metadata.put("likerName", liker.getFullName());
            metadata.put("likerAvatar", liker.getAvatar());
            metadata.put("postTitle", post.getTitle());

            notificationService.createNotification(
                    comment.getUser().getUserID(),
                    NotificationType.COMMENT_LIKE,
                    title,
                    message,
                    metadata
            );
            log.info("Like notification sent to user {} for comment {}", comment.getUser().getUserID(), comment.getCommentID());
        } catch (Exception e){
            log.error("Failed to send like notification");
        }
    }

    private PostListResponseDTO mapToPostListResponse(ForumPost post) {
        return PostListResponseDTO.builder()
                .postID(post.getPostID())
                .title(post.getTitle())
                .summary(post.getSummary())
                .thumbnailUrl(post.getThumbnailUrl())
                .postType(post.getPostType() != null ? post.getPostType().toString() : null)
                .authorId(post.getUser().getUserID())
                .authorName(post.getUser().getFullName())
                .authorAvatar(post.getUser().getAvatar())
                .categoryId(post.getCategory().getCategoryID())
                .categoryName(post.getCategory().getCategoryName())
                .categorySlug(post.getCategory().getSlug())
                .categoryColor(post.getCategory().getColor())
                .tags(post.getPostTags() != null ?
                        post.getPostTags().stream()
                                .map(pt -> PostListResponseDTO.TagInfo.builder()
                                        .tagId(pt.getTag().getTagID())
                                        .tagName(pt.getTag().getTagName())
                                        .color(pt.getTag().getColor())
                                        .build())
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .isPinned(post.getIsPinned())
                .isFeatured(post.getIsFeatured())
                .createdAt(post.getCreatedAt())
                .publishedAt(post.getPublishedAt())
                .build();
    }

    private PostDetailResponseDTO mapToDetailResponse(ForumPost post, String username) {
        boolean isLiked = false;
        boolean isBookmarked = false;

        if (username != null) {
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                Integer userId = user.getUserID();
                isLiked = post.getLikes() != null && post.getLikes().stream()
                        .anyMatch(like -> like.getUser().getUserID().equals(userId));
            }
        }

        return mapToDetailResponse(post, isLiked, isBookmarked);
    }

    private PostDetailResponseDTO mapToDetailResponse(ForumPost post, boolean isLikedByCurrentUser, boolean isBookmarkedByCurrentUser) {
        // Get root comments (no parent), sorted by newest first
        List<PostComment> rootComments = post.getComments() != null ?
                post.getComments().stream()
                        .filter(c -> c.getParentComment() == null && c.getStatus() == ContentStatus.PUBLISHED)
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        String currentUsername = getCurrentUsername();
        List<CommentResponseDTO> comments = rootComments.stream()
                .map(comment -> mapToCommentDTO(comment, currentUsername))
                .collect(Collectors.toList());

        // Build author info
        AuthorInfoDTO author = AuthorInfoDTO.builder()
                .userId(post.getUser().getUserID())
                .fullName(post.getUser().getFullName())
                .email(post.getUser().getEmail())
                .avatarUrl(post.getUser().getAvatar())
                .totalPosts(post.getUser().getPosts() != null ? post.getUser().getPosts().size() : 0)
                .totalFollowers(0)
                .reputationPoints(0)
                .build();

        // Build category info
        CategorySimpleDTO category = CategorySimpleDTO.builder()
                .categoryId(post.getCategory().getCategoryID())
                .categoryName(post.getCategory().getCategoryName())
                .slug(post.getCategory().getSlug())
                .icon(post.getCategory().getIcon())
                .color(post.getCategory().getColor())
                .build();

        // Build tags
        List<TagSimpleDTO> tags = post.getPostTags() != null ?
                post.getPostTags().stream()
                        .map(pt -> TagSimpleDTO.builder()
                                .tagId(pt.getTag().getTagID())
                                .tagName(pt.getTag().getTagName())
                                .slug(pt.getTag().getSlug())
                                .color(pt.getTag().getColor())
                                .build())
                        .collect(Collectors.toList())
                : new ArrayList<>();

        // Build images
        List<PostImageDTO> images = post.getImages() != null ?
                post.getImages().stream()
                        .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                        .map(img -> PostImageDTO.builder()
                                .imageId(img.getImageID())
                                .imageUrl(img.getImageUrl())
                                .caption(img.getAltText())
                                .altText(img.getAltText())
                                .displayOrder(img.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList())
                : new ArrayList<>();

        return PostDetailResponseDTO.builder()
                .postId(post.getPostID())
                .title(post.getTitle())
                .content(post.getContent())
                .summary(post.getSummary())
                .thumbnailUrl(post.getThumbnailUrl())
                .postType(post.getPostType() != null ? post.getPostType().toString() : null)
                .status(post.getStatus() != null ? post.getStatus().toString() : null)
                .author(author)
                .category(category)
                .tags(tags)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .shareCount(post.getShareCount())
                .isPinned(post.getIsPinned())
                .isFeatured(post.getIsFeatured())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .isBookmarkedByCurrentUser(isBookmarkedByCurrentUser)
                .isFollowingAuthor(false)
                .images(images)
                .comments(comments)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .build();
    }

    private CommentResponseDTO mapToCommentDTO(PostComment comment, String username) {
        boolean isLiked = false;

        if (username != null) {
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                Integer userId = user.getUserID();
                isLiked = comment.getLikes() != null && comment.getLikes().stream()
                        .anyMatch(like -> like.getUser().getUserID().equals(userId));
            }
        }

        List<CommentResponseDTO> replies = comment.getReplies() != null ?
                comment.getReplies().stream()
                        .filter(r -> r.getStatus() == ContentStatus.PUBLISHED)
                        .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                        .map(reply -> mapToCommentDTO(reply, username))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentID())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .isLikedByCurrentUser(isLiked)
                .createdAt(comment.getCreatedAt())
                .isEdited(comment.getIsEdited())
                .author(AuthorInfoDTO.builder()
                        .userId(comment.getUser().getUserID())
                        .fullName(comment.getUser().getFullName())
                        .avatarUrl(comment.getUser().getAvatar())
                        .build())
                .replies(replies)
                .build();
    }
}