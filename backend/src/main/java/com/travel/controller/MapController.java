package com.travel.controller;

import com.travel.dto.MapLayerResponse;
import com.travel.dto.MapMarkerResponse;
import com.travel.dto.MapRouteResponse;
import com.travel.entity.MapMarker;
import com.travel.entity.MapRoute;
import com.travel.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MapController {
    
    private final MapService mapService;
    
    // 获取所有地图标记
    @GetMapping("/markers")
    public ResponseEntity<List<MapMarkerResponse>> getAllMarkers() {
        log.info("获取所有地图标记");
        List<MapMarkerResponse> markers = mapService.getAllMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 根据类型获取标记
    @GetMapping("/markers/type/{type}")
    public ResponseEntity<List<MapMarkerResponse>> getMarkersByType(@PathVariable MapMarker.MarkerType type) {
        log.info("根据类型获取地图标记: {}", type);
        List<MapMarkerResponse> markers = mapService.getMarkersByType(type);
        return ResponseEntity.ok(markers);
    }
    
    // 根据城市获取标记
    @GetMapping("/markers/city/{city}")
    public ResponseEntity<List<MapMarkerResponse>> getMarkersByCity(@PathVariable String city) {
        log.info("根据城市获取地图标记: {}", city);
        List<MapMarkerResponse> markers = mapService.getMarkersByCity(city);
        return ResponseEntity.ok(markers);
    }
    
    // 获取热门标记
    @GetMapping("/markers/popular")
    public ResponseEntity<List<MapMarkerResponse>> getPopularMarkers() {
        log.info("获取热门地图标记");
        List<MapMarkerResponse> markers = mapService.getPopularMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 获取推荐标记
    @GetMapping("/markers/recommended")
    public ResponseEntity<List<MapMarkerResponse>> getRecommendedMarkers() {
        log.info("获取推荐地图标记");
        List<MapMarkerResponse> markers = mapService.getRecommendedMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 根据边界获取标记
    @GetMapping("/markers/bounds")
    public ResponseEntity<List<MapMarkerResponse>> getMarkersByBounds(
            @RequestParam BigDecimal minLat,
            @RequestParam BigDecimal maxLat,
            @RequestParam BigDecimal minLng,
            @RequestParam BigDecimal maxLng) {
        log.info("根据边界获取地图标记: lat[{}, {}], lng[{}, {}]", minLat, maxLat, minLng, maxLng);
        List<MapMarkerResponse> markers = mapService.getMarkersByBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(markers);
    }
    
    // 获取所有图层
    @GetMapping("/layers")
    public ResponseEntity<List<MapLayerResponse>> getAllLayers() {
        log.info("获取所有地图图层");
        List<MapLayerResponse> layers = mapService.getAllLayers();
        return ResponseEntity.ok(layers);
    }
    
    // 获取所有路线
    @GetMapping("/routes")
    public ResponseEntity<List<MapRouteResponse>> getAllRoutes() {
        log.info("获取所有地图路线");
        List<MapRouteResponse> routes = mapService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }
    
    // 根据路线类型获取路线
    @GetMapping("/routes/type/{type}")
    public ResponseEntity<List<MapRouteResponse>> getRoutesByType(@PathVariable MapRoute.RouteType type) {
        log.info("根据类型获取地图路线: {}", type);
        List<MapRouteResponse> routes = mapService.getRoutesByType(type);
        return ResponseEntity.ok(routes);
    }
    
    // 获取风景路线
    @GetMapping("/routes/scenic")
    public ResponseEntity<List<MapRouteResponse>> getScenicRoutes() {
        log.info("获取风景路线");
        List<MapRouteResponse> routes = mapService.getScenicRoutes();
        return ResponseEntity.ok(routes);
    }
}

