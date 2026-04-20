package com.travel.repository;

import com.travel.entity.TravelPlan;
import com.travel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    
    List<TravelPlan> findByUser(User user);
    
    List<TravelPlan> findByUserId(Long userId);
    
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.user.id = :userId AND tp.status = :status")
    List<TravelPlan> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TravelPlan.PlanStatus status);
    
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.aiGenerated = true ORDER BY tp.createdAt DESC")
    List<TravelPlan> findAiGeneratedPlans();
}



























