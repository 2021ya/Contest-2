package com.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "map_routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapRoute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal startLatitude;
    
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal startLongitude;
    
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal endLatitude;
    
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal endLongitude;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteType routeType;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal distanceKm;
    
    private Integer durationMinutes;
    
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    
    @Column(nullable = false)
    private Boolean isScenic = false;
    
    @Column(columnDefinition = "JSON")
    private String routeData;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum RouteType {
        WALKING, DRIVING, CYCLING, PUBLIC_TRANSPORT
    }
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}

