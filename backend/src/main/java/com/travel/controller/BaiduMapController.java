package com.travel.controller;

import com.travel.dto.BaiduMapMarkerRequest;
import com.travel.dto.BaiduMapMarkerResponse;
import com.travel.entity.BaiduMapMarker;
import com.travel.service.BaiduMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/baidu-map")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BaiduMapController {
    
    private final BaiduMapService baiduMapService;
    
    // 获取所有百度地图标记
    @GetMapping("/markers")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getAllMarkers() {
        log.info("获取所有百度地图标记");
        List<BaiduMapMarkerResponse> markers = baiduMapService.getAllMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 根据类型获取标记
    @GetMapping("/markers/type/{type}")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getMarkersByType(@PathVariable BaiduMapMarker.MarkerType type) {
        log.info("根据类型获取百度地图标记: {}", type);
        List<BaiduMapMarkerResponse> markers = baiduMapService.getMarkersByType(type);
        return ResponseEntity.ok(markers);
    }
    
    // 根据城市获取标记
    @GetMapping("/markers/city/{city}")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getMarkersByCity(@PathVariable String city) {
        log.info("根据城市获取百度地图标记: {}", city);
        List<BaiduMapMarkerResponse> markers = baiduMapService.getMarkersByCity(city);
        return ResponseEntity.ok(markers);
    }
    
    // 获取热门标记
    @GetMapping("/markers/popular")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getPopularMarkers() {
        log.info("获取热门百度地图标记");
        List<BaiduMapMarkerResponse> markers = baiduMapService.getPopularMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 获取推荐标记
    @GetMapping("/markers/recommended")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getRecommendedMarkers() {
        log.info("获取推荐百度地图标记");
        List<BaiduMapMarkerResponse> markers = baiduMapService.getRecommendedMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 获取精选标记
    @GetMapping("/markers/featured")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getFeaturedMarkers() {
        log.info("获取精选百度地图标记");
        List<BaiduMapMarkerResponse> markers = baiduMapService.getFeaturedMarkers();
        return ResponseEntity.ok(markers);
    }
    
    // 根据边界获取标记
    @GetMapping("/markers/bounds")
    public ResponseEntity<List<BaiduMapMarkerResponse>> getMarkersByBounds(
            @RequestParam BigDecimal minLat,
            @RequestParam BigDecimal maxLat,
            @RequestParam BigDecimal minLng,
            @RequestParam BigDecimal maxLng) {
        log.info("根据边界获取百度地图标记: lat[{}, {}], lng[{}, {}]", minLat, maxLat, minLng, maxLng);
        List<BaiduMapMarkerResponse> markers = baiduMapService.getMarkersByBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(markers);
    }
    
    // 搜索标记
    @GetMapping("/markers/search")
    public ResponseEntity<List<BaiduMapMarkerResponse>> searchMarkers(@RequestParam String keyword) {
        log.info("搜索百度地图标记: {}", keyword);
        List<BaiduMapMarkerResponse> markers = baiduMapService.searchMarkers(keyword);
        return ResponseEntity.ok(markers);
    }
    
    // 创建标记
    @PostMapping("/markers")
    public ResponseEntity<BaiduMapMarkerResponse> createMarker(@RequestBody BaiduMapMarkerRequest request) {
        log.info("创建百度地图标记: {}", request.getName());
        BaiduMapMarkerResponse marker = baiduMapService.createMarker(request);
        return ResponseEntity.ok(marker);
    }
    
    // 从百度地图API获取地点信息
    @GetMapping("/api/place")
    public ResponseEntity<BaiduMapMarkerResponse> getPlaceFromApi(
            @RequestParam String query,
            @RequestParam(required = false) String city) {
        log.info("从百度地图API获取地点信息: {}, 城市: {}", query, city);
        BaiduMapMarkerResponse marker = baiduMapService.getPlaceFromBaiduApi(query, city);
        return ResponseEntity.ok(marker);
    }
}

