package com.travel.dto;

import com.travel.entity.MapLayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayerResponse {
    
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private MapLayer.LayerType layerType;
    private Boolean isVisible;
    private Boolean isDefault;
    private Integer zIndex;
    private String styleConfig;
}

