package com.tourism.backend.repository;

import com.tourism.backend.entity.FavoriteTour;
import com.tourism.backend.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;
@Repository
public interface FavoriteTourRepository extends JpaRepository<FavoriteTour, Integer> {

    @Query("SELECT ft.tour.tourID FROM FavoriteTour ft WHERE ft.user.userID = :userId")
    Set<Integer> findTourIdsByUserId(@Param("userId") Integer userId);

    @Query("SELECT ft FROM FavoriteTour ft WHERE ft.user.userID = :userId AND ft.tour.tourID = :tourId")
    Optional<FavoriteTour> findByUserIdAndTourTourId(@Param("userId") Integer userId, @Param("tourId") Integer tourId);
    // 3. Xóa một bản ghi FavoriteTour cụ thể
    @Modifying
    @Query("DELETE FROM FavoriteTour ft WHERE ft.user.userID = :userId AND ft.tour.tourID = :tourId")
    int deleteByUserIdAndTourTourId(@Param("userId") Integer userId, @Param("tourId") Integer tourId);


    @Query("SELECT ft FROM FavoriteTour ft " +
            // Chỉ FETCH một Collection (departures)
            "LEFT JOIN FETCH ft.tour t " +
            "LEFT JOIN FETCH t.departures td " +
            "LEFT JOIN FETCH t.startLocation " +
            "WHERE ft.user.userID = :userId " +
            "ORDER BY ft.createdAt DESC")
    List<FavoriteTour> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
//@Query("""
//
//    SELECT ft
//    FROM FavoriteTour ft
//    LEFT JOIN FETCH ft.tour t
//    LEFT JOIN FETCH t.startLocation sl
//    LEFT JOIN FETCH t.images img
//    WHERE ft.user.userID = :userId
//    ORDER BY ft.createdAt DESC
//""")
//List<FavoriteTour> findFavoriteToursWithTourDetailsByUserId(@Param("userId") Integer userId);
}