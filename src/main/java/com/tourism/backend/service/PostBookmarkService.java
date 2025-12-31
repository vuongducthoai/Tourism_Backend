package com.tourism.backend.service;

import com.tourism.backend.dto.request.BookmarkRequestDTO;
import com.tourism.backend.dto.response.BookmarkResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostBookmarkService {
    void toggleBookmark(Integer postId, BookmarkRequestDTO request, String userName);
    boolean isBookmarked(Integer postId, String userName);
    Page<BookmarkResponseDTO> getUserBookmarks(String username, Pageable pageable);
    Page<BookmarkResponseDTO> getBookmarksByFolder(String username, String folderName, Pageable pageable);
    List<String> getUserFolders(String username);
    void deleteBookmark(Integer bookmarkId, String username);
}
