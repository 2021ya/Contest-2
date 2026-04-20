package com.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "map_markers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMarker {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 100)
    private String country;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarkerType type;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Enumerated(EnumType.STRING)
    private PriceLevel priceLevel;
    
    @Column(length = 100)
    private String openingHours;
    
    @Column(length = 50)
    private String phone;
    
    @Column(length = 255)
    private String website;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(length = 50)
    private String iconType;
    
    @Column(length = 20)
    private String color;
    
    @Enumerated(EnumType.STRING)
    private MarkerSize size;
    
    @Column(nullable = false)
    private Boolean isPopular = false;
    
    @Column(nullable = false)
    private Boolean isRecommended = false;
    
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
    
    public enum MarkerType {
        ATTRACTION, HOTEL, RESTAURANT, TRANSPORT, SHOPPING, ENTERTAINMENT, NATURE, CULTURE, HISTORY
    }
    
    public enum PriceLevel {
        FREE, LOW, MEDIUM, HIGH, LUXURY
    }
    
    public enum MarkerSize {
        SMALL, MEDIUM, LARGE
    }
}

