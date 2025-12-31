package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.BookmarkRequestDTO;
import com.tourism.backend.dto.response.BookmarkResponseDTO;
import com.tourism.backend.entity.ForumPost;
import com.tourism.backend.entity.PostBookmark;
import com.tourism.backend.entity.User;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.ForumPostRepository;
import com.tourism.backend.repository.PostBookmarkRepository;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.ForumPostService;
import com.tourism.backend.service.PostBookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostBookmarkServiceImpl implements PostBookmarkService {
    private final PostBookmarkRepository bookmarkRepository;
    private final ForumPostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void toggleBookmark(Integer postId, BookmarkRequestDTO request, String userName) {
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Optional<PostBookmark> existingBookmark = bookmarkRepository
                .findByUserAndPost(user.getUserID(), postId);

        if(existingBookmark.isPresent()){
            bookmarkRepository.delete(existingBookmark.get());
            post.setBookmarkCount(Math.max(0, post.getBookmarkCount() - 1));
            postRepository.save(post);
            log.info("User {} removed bookmark from post {}", user.getUserID(), postId);
        } else {
            PostBookmark bookmark = new PostBookmark();
            bookmark.setPost(post);
            bookmark.setUser(user);
            bookmark.setFolderName(request.getFolderName());
            bookmark.setNote(request.getNote());
            bookmark.setCreatedAt(LocalDateTime.now());
            bookmarkRepository.save(bookmark);

            post.setBookmarkCount(post.getBookmarkCount() + 1);
            postRepository.save(post);
            log.info("User {} bookmarked post {}", user.getUserID(), postId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Integer postId, String username) {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) return false;

        return bookmarkRepository
                .findByUserAndPost(user.getUserID(), postId)
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkResponseDTO> getUserBookmarks(String username, Pageable pageable) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<PostBookmark> bookmarks = bookmarkRepository.findByUserId(user.getUserID(), pageable);

        return bookmarks.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkResponseDTO> getBookmarksByFolder(String username, String folderName, Pageable pageable) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<PostBookmark> bookmarks = bookmarkRepository
                .findByUserIdAndFolder(user.getUserID(), folderName, pageable);

        return bookmarks.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserFolders(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookmarkRepository.findFoldersByUserId(user.getUserID());
    }

    @Override
    @Transactional
    public void deleteBookmark(Integer bookmarkId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PostBookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));

        if (!bookmark.getUser().getUserID().equals(user.getUserID())) {
            throw new IllegalArgumentException("Cannot delete other user's bookmark");
        }

        ForumPost post = bookmark.getPost();
        post.setBookmarkCount(Math.max(0, post.getBookmarkCount() - 1));
        postRepository.save(post);

        bookmarkRepository.delete(bookmark);
        log.info("Deleted bookmark {} for user {}", bookmarkId, user.getUserID());
    }

    private BookmarkResponseDTO mapToDTO(PostBookmark bookmark) {
        ForumPost post = bookmark.getPost();

        return BookmarkResponseDTO.builder()
                .bookmarkId(bookmark.getBookmarkID())
                .postId(post.getPostID())
                .postTitle(post.getTitle())
                .postSummary(post.getSummary())
                .postThumbnail(post.getThumbnailUrl())
                .authorName(post.getUser().getFullName())
                .folderName(bookmark.getFolderName())
                .note(bookmark.getNote())
                .bookmarkedAt(bookmark.getCreatedAt())
                .build();
    }
}
