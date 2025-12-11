package com.tourism.backend.controller;

import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.service.FavoriteTourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-tours")
@RequiredArgsConstructor
public class FavoriteTourController {
    private final FavoriteTourService favoriteTourService;

    // API thêm tour vào danh sách yêu thích
    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteTour(
            @RequestParam("userId") Integer userId,
            @RequestParam("tourId") Integer tourId) {
        try {
            favoriteTourService.addFavoriteTour(userId, tourId);
            return ResponseEntity.ok("Tour added to favorites successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to add favorite tour.");
        }
    }

    // API xóa tour khỏi danh sách yêu thích
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFavoriteTour(
            @RequestParam("userId") Integer userId,
            @RequestParam("tourId") Integer tourId) {
        try {
            favoriteTourService.removeFavoriteTour(userId, tourId);
            return ResponseEntity.ok("Tour removed from favorites successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to remove favorite tour.");
        }
    }

    //  Lấy danh sách tour yêu thích của user ✨
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFavoriteTours(@PathVariable Integer userId) {
//        try {
            List<TourResponseDTO> favoriteTours = favoriteTourService.getUserFavoriteTours(userId);
            return ResponseEntity.ok(favoriteTours);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Failed to fetch favorite tours.");
//        }
    }
}