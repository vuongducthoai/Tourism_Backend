package com.tourism.backend.repository;

import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}