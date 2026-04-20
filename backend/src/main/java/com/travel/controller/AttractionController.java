package com.travel.controller;

import com.travel.dto.AttractionResponse;
import com.travel.entity.Attraction;
import com.travel.service.AttractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attractions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AttractionController {
    
    private final AttractionService attractionService;
    
    @GetMapping
    public ResponseEntity<List<AttractionResponse>> getAllAttractions() {
        log.info("获取所有景点列表");
        List<AttractionResponse> attractions = attractionService.getAllAttractions();
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/recommended")
    public ResponseEntity<List<AttractionResponse>> getRecommendedAttractions() {
        log.info("获取推荐景点列表");
        List<AttractionResponse> attractions = attractionService.getRecommendedAttractions();
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<AttractionResponse>> getAttractionsByType(
            @PathVariable Attraction.AttractionType type) {
        log.info("根据类型获取景点列表: {}", type);
        List<AttractionResponse> attractions = attractionService.getAttractionsByType(type);
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<AttractionResponse>> getNearbyAttractions(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double radiusKm) {
        log.info("获取附近景点: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        List<AttractionResponse> attractions = attractionService.getNearbyAttractions(latitude, longitude, radiusKm);
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<AttractionResponse>> searchAttractions(
            @RequestParam String keyword) {
        log.info("搜索景点: {}", keyword);
        List<AttractionResponse> attractions = attractionService.searchAttractions(keyword);
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AttractionResponse> getAttractionById(@PathVariable Long id) {
        log.info("获取景点详情: {}", id);
        try {
            AttractionResponse attraction = attractionService.getAttractionById(id);
            return ResponseEntity.ok(attraction);
        } catch (RuntimeException e) {
            log.error("获取景点失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<AttractionResponse>> getTopRatedAttractions(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取高评分景点: limit={}", limit);
        List<AttractionResponse> attractions = attractionService.getTopRatedAttractions(limit);
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/free")
    public ResponseEntity<List<AttractionResponse>> getFreeAttractions() {
        log.info("获取免费景点列表");
        List<AttractionResponse> attractions = attractionService.getFreeAttractions();
        return ResponseEntity.ok(attractions);
    }
}

