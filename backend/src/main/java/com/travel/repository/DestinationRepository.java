package com.travel.repository;

import com.travel.entity.Destination;
import com.travel.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    
    List<Destination> findByTravelPlan(TravelPlan travelPlan);
    
    List<Destination> findByTravelPlanId(Long travelPlanId);
    
    @Query("SELECT d FROM Destination d WHERE d.travelPlan.id = :planId ORDER BY d.dayOrder, d.timeOrder")
    List<Destination> findByTravelPlanIdOrderByDayAndTime(@Param("planId") Long planId);
    
    @Query("SELECT d FROM Destination d WHERE d.city = :city AND d.type = :type")
    List<Destination> findByCityAndType(@Param("city") String city, @Param("type") Destination.DestinationType type);
    
    @Query("SELECT d FROM Destination d WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * " +
           "cos(radians(d.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(d.latitude))) < :radius")
    List<Destination> findNearbyDestinations(@Param("lat") Double latitude, 
                                           @Param("lng") Double longitude, 
                                           @Param("radius") Double radiusKm);
}



























