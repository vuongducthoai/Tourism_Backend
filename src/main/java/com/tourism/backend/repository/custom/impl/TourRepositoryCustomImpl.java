package com.tourism.backend.repository.custom.impl;

import com.tourism.backend.dto.requestDTO.SearchToursRequestDTO;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.repository.custom.TourRepositoryCustom;
import com.tourism.backend.enums.PassengerType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public class TourRepositoryCustomImpl implements TourRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tour> searchToursDynamically(SearchToursRequestDTO dto) {

        BigDecimal minPrice = dto.getStartPrice() != null ? new BigDecimal(dto.getStartPrice()) : null;
        BigDecimal maxPrice = dto.getEndPrice() != null ? new BigDecimal(dto.getEndPrice()) : null;

        String nameParam = dto.getSearchNameTour();
        String transportationParam = dto.getTransportation();
        Integer startLocIdParam = dto.getStartLocationID();
        Integer endLocIdParam = dto.getEndLocationID();

        // Xây dựng truy vấn JPQL
        String jpql = """
            SELECT DISTINCT t 
            FROM Tour t
            LEFT JOIN FETCH t.startLocation sl
            LEFT JOIN FETCH t.images img 
            
            WHERE (img.isMainImage = TRUE OR img IS NULL)
            
            AND (:nameParam IS NULL OR LOWER(t.tourName) LIKE LOWER(CONCAT('%', CAST(:nameParam AS string), '%')))
            
            AND (:startLocIdParam IS NULL OR t.startLocation.locationID = :startLocIdParam)
            
            AND (:endLocIdParam IS NULL OR t.endLocation.locationID = :endLocIdParam)
            
            AND (:transportationParam IS NULL OR t.transportation LIKE CONCAT('%', CAST(:transportationParam AS string), '%'))
            
            AND t.tourID IN (
                SELECT td_sub.tour.tourID
                FROM TourDeparture td_sub
                JOIN td_sub.pricings dp_sub
                WHERE dp_sub.passengerType = com.tourism.backend.enums.PassengerType.ADULT
                
                GROUP BY td_sub.tour.tourID 
                
                HAVING (
                    (:minPrice IS NULL OR MIN(dp_sub.originalPrice) >= :minPrice)
                    AND
                    (:maxPrice IS NULL OR MIN(dp_sub.originalPrice) <= :maxPrice)
                )
            )
            
            ORDER BY t.tourID
        """;

        // Tạo và thiết lập tham số cho truy vấn
        // ... (phần thiết lập tham số giữ nguyên) ...
        jakarta.persistence.TypedQuery<Tour> query = entityManager.createQuery(jpql, Tour.class)
                .setParameter("nameParam", nameParam)
                .setParameter("transportationParam", transportationParam)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .setParameter("startLocIdParam", startLocIdParam)
                .setParameter("endLocIdParam", endLocIdParam);

        return query.getResultList();
    }
}