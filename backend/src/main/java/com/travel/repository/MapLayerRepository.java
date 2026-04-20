package com.travel.repository;

import com.travel.entity.MapLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapLayerRepository extends JpaRepository<MapLayer, Long> {
    
    List<MapLayer> findByIsVisibleTrue();
    
    List<MapLayer> findByIsDefaultTrue();
    
    List<MapLayer> findByLayerType(MapLayer.LayerType layerType);
}
