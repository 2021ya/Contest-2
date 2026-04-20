package com.travel.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Destination {
    
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
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "country")
    private String country;
    
    @Enumerated(EnumType.STRING)
    private DestinationType type;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "visit_duration")
    private Integer visitDuration; // 参观时长（分钟）
    
    @Column(name = "estimated_cost")
    private Double estimatedCost;
    
    @Column(name = "day_order")
    private Integer dayOrder; // 第几天
    
    @Column(name = "time_order")
    private Integer timeOrder; // 当天的第几个景点
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id")
    private TravelPlan travelPlan;
    
    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attraction> attractions;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum DestinationType {
        CITY, ATTRACTION, RESTAURANT, HOTEL, SHOPPING, ENTERTAINMENT, NATURE, CULTURE, HISTORY
    }
}



























