package com.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "baidu_map_markers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiduMapMarker {
    
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
    private String province;
    
    @Column(length = 100)
    @Builder.Default
    private String country = "中国";
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarkerType type;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "price_level")
    private PriceLevel priceLevel;
    
    @Column(name = "opening_hours", length = 100)
    private String openingHours;
    
    @Column(length = 50)
    private String phone;
    
    @Column(length = 255)
    private String website;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "icon_type", length = 50)
    private String iconType;
    
    @Column(length = 20)
    @Builder.Default
    private String color = "#FF6B6B";
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MarkerSize size = MarkerSize.MEDIUM;
    
    @Column(name = "is_popular", nullable = false)
    @Builder.Default
    private Boolean isPopular = false;
    
    @Column(name = "is_recommended", nullable = false)
    @Builder.Default
    private Boolean isRecommended = false;
    
    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;
    
    @Column(name = "visit_count")
    @Builder.Default
    private Integer visitCount = 0;
    
    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;
    
    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;
    
    @Column(name = "baidu_uid", length = 100)
    private String baiduUid;
    
    @Column(name = "baidu_poi_id", length = 100)
    private String baiduPoiId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
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
        ATTRACTION, HOTEL, RESTAURANT, TRANSPORT, SHOPPING, ENTERTAINMENT, 
        NATURE, CULTURE, HISTORY, SCENIC_SPOT, MUSEUM, PARK, TEMPLE, 
        BEACH, MOUNTAIN, LAKE, BUILDING, MONUMENT, MALL, THEATER, 
        ZOO, AQUARIUM, GARDEN
    }
    
    public enum PriceLevel {
        FREE, LOW, MEDIUM, HIGH, LUXURY
    }
    
    public enum MarkerSize {
        SMALL, MEDIUM, LARGE
    }
}
