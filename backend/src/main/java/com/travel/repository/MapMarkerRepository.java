package com.travel.repository;

import com.travel.entity.MapMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MapMarkerRepository extends JpaRepository<MapMarker, Long> {
    
    List<MapMarker> findByType(MapMarker.MarkerType type);
    
    List<MapMarker> findByCity(String city);
    
    List<MapMarker> findByIsPopularTrue();
    
    List<MapMarker> findByIsRecommendedTrue();
    
    @Query("SELECT m FROM MapMarker m WHERE m.latitude BETWEEN :minLat AND :maxLat AND m.longitude BETWEEN :minLng AND :maxLng")
    List<MapMarker> findByBounds(@Param("minLat") BigDecimal minLat, 
                                @Param("maxLat") BigDecimal maxLat,
                                @Param("minLng") BigDecimal minLng, 
                                @Param("maxLng") BigDecimal maxLng);
    
    @Query("SELECT m FROM MapMarker m WHERE m.type = :type AND m.city = :city")
    List<MapMarker> findByTypeAndCity(@Param("type") MapMarker.MarkerType type, @Param("city") String city);
    
    @Query("SELECT m FROM MapMarker m WHERE m.rating >= :minRating ORDER BY m.rating DESC")
    List<MapMarker> findByRatingGreaterThanEqualOrderByRatingDesc(@Param("minRating") BigDecimal minRating);
}

