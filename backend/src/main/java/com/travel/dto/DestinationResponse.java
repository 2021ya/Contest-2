package com.travel.dto;

import com.travel.entity.Destination;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationResponse {
    
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String country;
    private Destination.DestinationType type;
    private Double rating;
    private Integer visitDuration;
    private Double estimatedCost;
    private Integer dayOrder;
    private Integer timeOrder;
    private List<AttractionResponse> attractions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



























