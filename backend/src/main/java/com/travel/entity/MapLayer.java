package com.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "map_layers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 100)
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LayerType layerType;
    
    @Column(nullable = false)
    private Boolean isVisible = true;
    
    @Column(nullable = false)
    private Boolean isDefault = false;
    
    @Column(nullable = false)
    private Integer zIndex = 0;
    
    @Column(columnDefinition = "JSON")
    private String styleConfig;
    
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
    
    public enum LayerType {
        MARKER, POLYGON, LINE, HEATMAP
    }
}

