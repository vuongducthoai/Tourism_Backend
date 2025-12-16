package com.tourism.backend.repository;

import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByName(String name);
    List<Location> findByRegion(Region region);
    @Query("""
        SELECT DISTINCT l 
        FROM Location l 
        WHERE l.region = :region  
        AND l.locationID IN (
            SELECT t.endLocation.locationID 
            FROM Tour t
        )
        """)
    List<Location> findUniqueEndLocations(Region region);

    @Query("""
        SELECT DISTINCT t.endLocation 
        FROM Tour t
        """)
    List<Location> findUniqueEndLocations();

    @Query("""
        SELECT DISTINCT t.startLocation 
        FROM Tour t
        """)
    List<Location> findUniqueStartLocations();

    Optional<Location> findByAirportCode(String airportCode);

    boolean existsByName(String name);

    @Query("SELECT COUNT(l) > 0 FROM Location l WHERE l.name = :name AND l.locationID != :excludeId")
    boolean existsByNameAndNotId(@Param("name") String name, @Param("excludeId") Integer excludeId);

    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(l) > 0 FROM Location l WHERE l.slug = :slug AND l.locationID != :excludeId")
    boolean existsBySlugAndNotId(@Param("slug") String slug, @Param("excludeId") Integer excludeId);

    Optional<Location> findBySlug(String slug);

    @Query("SELECT l FROM Location l WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            " LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(l.slug) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(l.airportCode) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:region IS NULL OR l.region = :region)")
    Page<Location> searchLocations(
            @Param("search") String search,
            @Param("region") Region region,
            Pageable pageable
    );

    @Query("SELECT COUNT(t) FROM Tour t WHERE t.startLocation.locationID = :locationId")
    Long countToursAsStartPoint(@Param("locationId") Integer locationId);

    @Query("SELECT COUNT(t) FROM Tour t WHERE t.endLocation.locationID = :locationId")
    Long countToursAsEndPoint(@Param("locationId") Integer locationId);

    List<Location> findAllByStatusTrue();
    @Query("SELECT DISTINCT l FROM Location l " +
            "JOIN Tour t ON (t.startLocation.locationID = l.locationID OR t.endLocation.locationID = l.locationID) " +
            "WHERE t.status = true")
    List<Location> findLocationsWithActiveTours();
}