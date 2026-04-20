package com.travel.dto;

import com.travel.entity.MapMarker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMarkerResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String country;
    private MapMarker.MarkerType type;
    private BigDecimal rating;
    private MapMarker.PriceLevel priceLevel;
    private String openingHours;
    private String phone;
    private String website;
    private String imageUrl;
    private String iconType;
    private String color;
    private MapMarker.MarkerSize size;
    private Boolean isPopular;
    private Boolean isRecommended;
}

