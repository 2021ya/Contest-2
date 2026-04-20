package com.travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanRequest {
    
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    private String destination;
    private Integer days;
    private List<String> interests;
    private String travelStyle; // 休闲、冒险、文化、美食等
    private Integer groupSize;
    private String accommodationType; // 酒店、民宿、青旅等
    private String transportation; // 飞机、火车、自驾等
}



























