package com.travel.dto;

import com.travel.entity.BaiduMapMarker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiduMapMarkerResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String province;
    private String country;
    private BaiduMapMarker.MarkerType type;
    private BigDecimal rating;
    private BaiduMapMarker.PriceLevel priceLevel;
    private String openingHours;
    private String phone;
    private String website;
    private String imageUrl;
    private String iconType;
    private String color;
    private BaiduMapMarker.MarkerSize size;
    private Boolean isPopular;
    private Boolean isRecommended;
    private Boolean isFeatured;
    private Integer visitCount;
    private Integer likeCount;
    private Integer commentCount;
    private String baiduUid;
    private String baiduPoiId;
}

