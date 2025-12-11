package com.tourism.backend.service.impl;

import com.tourism.backend.convert.TourConvert;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.FavoriteTour;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.User;
import com.tourism.backend.repository.FavoriteTourRepository;
import com.tourism.backend.repository.TourRepository;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.FavoriteTourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteTourServiceImpl implements FavoriteTourService {

    private final FavoriteTourRepository favoriteTourRepository;
    private final UserRepository userRepository; // Cần Repository của User (giả định có UserRepo)
    private final TourRepository tourRepository;
    private final TourConvert tourConvert;
    @Override
    public Set<Integer> getFavoriteTourIdsByUserId(Integer userId) {
        return favoriteTourRepository.findTourIdsByUserId(userId);
    }

    @Override
    @Transactional
    public FavoriteTour addFavoriteTour(Integer userId, Integer tourId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found with ID: " + tourId));

        // Tránh trùng lặp
        Optional<FavoriteTour> existingFav = favoriteTourRepository.findByUserIdAndTourTourId(userId, tourId);
        if (existingFav.isPresent()) {
            return existingFav.get(); // Đã có, không cần thêm
        }

        FavoriteTour favoriteTour = new FavoriteTour();
        favoriteTour.setUser(user);
        favoriteTour.setTour(tour);

        return favoriteTourRepository.save(favoriteTour);
    }

    @Override
    @Transactional
    public void removeFavoriteTour(Integer userId, Integer tourId) {
        int deletedCount = favoriteTourRepository.deleteByUserIdAndTourTourId(userId, tourId);
        if (deletedCount == 0) {
            // Có thể bỏ qua hoặc ném ngoại lệ nếu tour yêu thích không tồn tại
            System.out.println("Warning: Favorite tour not found for User ID: " + userId + " and Tour ID: " + tourId);
        }
    }

    @Override
    public boolean isTourFavorite(Integer userId, Integer tourId) {
        return favoriteTourRepository.findByUserIdAndTourTourId(userId, tourId).isPresent();
    }

    // ✨ IMPLEMENTATION MỚI: Lấy danh sách tour yêu thích ✨
    @Override
    @Transactional(readOnly = true)
    public List<TourResponseDTO> getUserFavoriteTours(Integer userId) {
        // 1. Lấy danh sách FavoriteTour của user
        List<FavoriteTour> favoriteTours = favoriteTourRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // 2. Map sang TourResponseDTO
        return favoriteTours.stream()
                .map(ft -> {
                    Tour tour = ft.getTour();
                    TourResponseDTO dto = tourConvert.convertToTourReponsetoryDTO(tour);
                    dto.setIsFavorite(true); // Đánh dấu là đã yêu thích
                    return dto;
                })
                .collect(Collectors.toList());
    }
}