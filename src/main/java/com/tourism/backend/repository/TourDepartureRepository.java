package com.tourism.backend.repository;

import com.tourism.backend.entity.TourDeparture;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Integer> {
    @Query("SELECT d FROM TourDeparture d " +
            "JOIN d.transports t " + // Join với bảng vận chuyển
            "WHERE d.tour.tourCode = :tourCode " +
            "AND t.type = 'OUTBOUND' " + // Chỉ lấy chiều đi
            "AND t.departTime > CURRENT_TIMESTAMP " + // Lớn hơn thời điểm hiện tại
            "ORDER BY t.departTime ASC") // Sắp xếp tăng dần
    List<TourDeparture> findFutureDepartures(@Param("tourCode") String tourCode);
}
