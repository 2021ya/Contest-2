package com.travel.dto;

import com.travel.entity.ProvinceAttraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceAttractionResponse {
    
    private Long id;
    private String provinceName;
    private String attractionName;
    private String description;
    private String imageUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private ProvinceAttraction.AttractionType attractionType;
    private BigDecimal rating;
    private Integer visitDuration;
    private BigDecimal ticketPrice;
    private String openingHours;
    private String phone;
    private String website;
    private String bestSeason;
    private ProvinceAttraction.DifficultyLevel difficultyLevel;
    private Boolean isPopular;
    private Boolean isRecommended;
}

