package com.travel.dto;

import com.travel.entity.Attraction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResponse {
    
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private Attraction.AttractionType type;
    private Double rating;
    private Integer visitDuration;
    private Double ticketPrice;
    private String openingHours;
    private String phone;
    private String website;
    private String imageUrl;
    private Long destinationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



























