package com.travel.controller;

import com.travel.dto.ProvinceAttractionResponse;
import com.travel.entity.ProvinceAttraction;
import com.travel.service.ProvinceAttractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/province-attractions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProvinceAttractionController {
    
    private final ProvinceAttractionService provinceAttractionService;
    
    // 获取所有省份景点
    @GetMapping
    public ResponseEntity<List<ProvinceAttractionResponse>> getAllAttractions() {
        log.info("获取所有省份景点");
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getAllAttractions();
        return ResponseEntity.ok(attractions);
    }
    
    // 根据省份获取景点
    @GetMapping("/province/{provinceName}")
    public ResponseEntity<List<ProvinceAttractionResponse>> getAttractionsByProvince(@PathVariable String provinceName) {
        log.info("根据省份获取景点: {}", provinceName);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getAttractionsByProvince(provinceName);
        return ResponseEntity.ok(attractions);
    }
    
    // 根据城市获取景点
    @GetMapping("/city/{city}")
    public ResponseEntity<List<ProvinceAttractionResponse>> getAttractionsByCity(@PathVariable String city) {
        log.info("根据城市获取景点: {}", city);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getAttractionsByCity(city);
        return ResponseEntity.ok(attractions);
    }
    
    // 根据类型获取景点
    @GetMapping("/type/{attractionType}")
    public ResponseEntity<List<ProvinceAttractionResponse>> getAttractionsByType(@PathVariable ProvinceAttraction.AttractionType attractionType) {
        log.info("根据类型获取景点: {}", attractionType);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getAttractionsByType(attractionType);
        return ResponseEntity.ok(attractions);
    }
    
    // 获取热门景点
    @GetMapping("/popular")
    public ResponseEntity<List<ProvinceAttractionResponse>> getPopularAttractions() {
        log.info("获取热门景点");
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getPopularAttractions();
        return ResponseEntity.ok(attractions);
    }
    
    // 获取推荐景点
    @GetMapping("/recommended")
    public ResponseEntity<List<ProvinceAttractionResponse>> getRecommendedAttractions() {
        log.info("获取推荐景点");
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getRecommendedAttractions();
        return ResponseEntity.ok(attractions);
    }
    
    // 获取省份热门景点
    @GetMapping("/province/{provinceName}/popular")
    public ResponseEntity<List<ProvinceAttractionResponse>> getPopularAttractionsByProvince(@PathVariable String provinceName) {
        log.info("获取省份热门景点: {}", provinceName);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getPopularAttractionsByProvince(provinceName);
        return ResponseEntity.ok(attractions);
    }
    
    // 获取省份推荐景点
    @GetMapping("/province/{provinceName}/recommended")
    public ResponseEntity<List<ProvinceAttractionResponse>> getRecommendedAttractionsByProvince(@PathVariable String provinceName) {
        log.info("获取省份推荐景点: {}", provinceName);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getRecommendedAttractionsByProvince(provinceName);
        return ResponseEntity.ok(attractions);
    }
    
    // 获取省份特定类型景点
    @GetMapping("/province/{provinceName}/type/{attractionType}")
    public ResponseEntity<List<ProvinceAttractionResponse>> getAttractionsByProvinceAndType(
            @PathVariable String provinceName, 
            @PathVariable ProvinceAttraction.AttractionType attractionType) {
        log.info("获取省份特定类型景点: {} - {}", provinceName, attractionType);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getAttractionsByProvinceAndType(provinceName, attractionType);
        return ResponseEntity.ok(attractions);
    }
    
    // 获取所有省份列表
    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getAllProvinces() {
        log.info("获取所有省份列表");
        List<String> provinces = provinceAttractionService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }
    
    // 获取高评分景点
    @GetMapping("/high-rated")
    public ResponseEntity<List<ProvinceAttractionResponse>> getHighRatedAttractions(@RequestParam(defaultValue = "4.0") BigDecimal minRating) {
        log.info("获取高评分景点: {}", minRating);
        List<ProvinceAttractionResponse> attractions = provinceAttractionService.getHighRatedAttractions(minRating);
        return ResponseEntity.ok(attractions);
    }
}

