package com.travel.repository;

import com.travel.entity.Attraction;
import com.travel.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    
    List<Attraction> findByDestination(Destination destination);
    
    List<Attraction> findByDestinationId(Long destinationId);
    
    @Query("SELECT a FROM Attraction a WHERE a.type = :type ORDER BY a.rating DESC")
    List<Attraction> findByTypeOrderByRating(@Param("type") Attraction.AttractionType type);
    
    @Query("SELECT a FROM Attraction a WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(a.latitude))) < :radius")
    List<Attraction> findNearbyAttractions(@Param("lat") Double latitude, 
                                         @Param("lng") Double longitude, 
                                         @Param("radius") Double radiusKm);
    
    @Query("SELECT a FROM Attraction a WHERE a.name LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<Attraction> searchByNameOrDescription(@Param("keyword") String keyword);
}



























