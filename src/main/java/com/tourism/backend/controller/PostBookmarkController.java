package com.tourism.backend.controller;

import com.tourism.backend.dto.request.BookmarkRequestDTO;
import com.tourism.backend.dto.response.ApiResponse;
import com.tourism.backend.dto.response.BookmarkResponseDTO;
import com.tourism.backend.service.PostBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class PostBookmarkController {

    private final PostBookmarkService bookmarkService;

    @PostMapping("/{postId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable Integer postId,
            @RequestBody(required = false) BookmarkRequestDTO request,
            Authentication authentication) {

        String username = authentication.getName();

        if (request == null) {
            request = new BookmarkRequestDTO();
        }

        bookmarkService.toggleBookmark(postId, request, username);
        boolean isBookmarked = bookmarkService.isBookmarked(postId, username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isBookmarked", isBookmarked);
        response.put("message", isBookmarked ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/check")
    public ResponseEntity<Map<String, Boolean>> checkBookmark(
            @PathVariable Integer postId,
            Authentication authentication) {

        String username = authentication.getName();
        boolean isBookmarked = bookmarkService.isBookmarked(postId, username);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/me")
    public ResponseEntity<Page<BookmarkResponseDTO>> getMyBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<BookmarkResponseDTO> bookmarks = bookmarkService.getUserBookmarks(username, pageable);

        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/me/folder/{folderName}")
    public ResponseEntity<Page<BookmarkResponseDTO>> getBookmarksByFolder(
            @PathVariable String folderName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<BookmarkResponseDTO> bookmarks = bookmarkService
                .getBookmarksByFolder(username, folderName, pageable);

        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/me/folders")
    public ResponseEntity<Map<String, List<String>>> getMyFolders(Authentication authentication) {
        String username = authentication.getName();
        List<String> folders = bookmarkService.getUserFolders(username);

        Map<String, List<String>> response = new HashMap<>();
        response.put("folders", folders);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Map<String, String>> deleteBookmark(
            @PathVariable Integer bookmarkId,
            Authentication authentication) {

        String username = authentication.getName();
        bookmarkService.deleteBookmark(bookmarkId, username);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Đã xóa bookmark thành công");

        return ResponseEntity.ok(response);
    }
}