package com.travel.dto;

import com.travel.entity.MapRoute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapRouteResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;
    private MapRoute.RouteType routeType;
    private BigDecimal distanceKm;
    private Integer durationMinutes;
    private MapRoute.Difficulty difficulty;
    private Boolean isScenic;
    private String routeData;
}

