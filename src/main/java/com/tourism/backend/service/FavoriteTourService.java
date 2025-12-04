package com.tourism.backend.service;

import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.FavoriteTour;

import java.util.List;
import java.util.Set;

public interface FavoriteTourService {
    Set<Integer> getFavoriteTourIdsByUserId(Integer userId);
    FavoriteTour addFavoriteTour(Integer userId, Integer tourId);
    void removeFavoriteTour(Integer userId, Integer tourId);
    boolean isTourFavorite(Integer userId, Integer tourId);
    List<TourResponseDTO> getUserFavoriteTours(Integer userId);
}