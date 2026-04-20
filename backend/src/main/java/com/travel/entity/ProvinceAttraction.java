package com.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "province_attractions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceAttraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String provinceName;
    
    @Column(nullable = false, length = 200)
    private String attractionName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttractionType attractionType;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    private Integer visitDuration;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal ticketPrice;
    
    @Column(length = 100)
    private String openingHours;
    
    @Column(length = 50)
    private String phone;
    
    @Column(length = 255)
    private String website;
    
    @Column(length = 50)
    private String bestSeason;
    
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
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
    
    public enum AttractionType {
        NATURAL, HISTORICAL, CULTURAL, MODERN, RELIGIOUS, ENTERTAINMENT, SHOPPING, FOOD
    }
    
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}

