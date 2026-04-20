package com.travel.dto;

import com.travel.entity.TravelPlan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanResponse {
    
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    private TravelPlan.PlanStatus status;
    private Boolean aiGenerated;
    private Long userId;
    private List<DestinationResponse> destinations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



























