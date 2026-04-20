package com.travel.repository;

import com.travel.entity.MapRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MapRouteRepository extends JpaRepository<MapRoute, Long> {
    
    List<MapRoute> findByRouteType(MapRoute.RouteType routeType);
    
    List<MapRoute> findByIsScenicTrue();
    
    List<MapRoute> findByDifficulty(MapRoute.Difficulty difficulty);
    
    @Query("SELECT r FROM MapRoute r WHERE r.startLatitude BETWEEN :minLat AND :maxLat AND r.startLongitude BETWEEN :minLng AND :maxLng")
    List<MapRoute> findByStartLocationBounds(@Param("minLat") BigDecimal minLat, 
                                           @Param("maxLat") BigDecimal maxLat,
                                           @Param("minLng") BigDecimal minLng, 
                                           @Param("maxLng") BigDecimal maxLng);
    
    @Query("SELECT r FROM MapRoute r WHERE r.routeType = :routeType AND r.isScenic = true")
    List<MapRoute> findByRouteTypeAndIsScenicTrue(@Param("routeType") MapRoute.RouteType routeType);
}

