package com.travel.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "attractions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Attraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    
    @Column(name = "address")
    private String address;
    
    @Enumerated(EnumType.STRING)
    private AttractionType type;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "visit_duration")
    private Integer visitDuration; // 参观时长（分钟）
    
    @Column(name = "ticket_price")
    private Double ticketPrice;
    
    @Column(name = "opening_hours")
    private String openingHours;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = true)
    private Destination destination;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum AttractionType {
        MUSEUM, PARK, TEMPLE, BEACH, MOUNTAIN, LAKE, BUILDING, MONUMENT, 
        SHOPPING_MALL, RESTAURANT, HOTEL, THEATER, ZOO, AQUARIUM, GARDEN,
        CULTURE, HISTORY, ATTRACTION, HISTORIC, RELIGIOUS, CULTURAL, FOOD,
        SHOPPING, ENTERTAINMENT, THEME_PARK, SPORTS, NIGHTLIFE
    }
}



























