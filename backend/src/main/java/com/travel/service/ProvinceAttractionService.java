package com.travel.service;

import com.travel.dto.ProvinceAttractionResponse;
import com.travel.entity.ProvinceAttraction;
import com.travel.repository.ProvinceAttractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvinceAttractionService {
    
    private final ProvinceAttractionRepository provinceAttractionRepository;
    
    // 获取所有省份景点
    public List<ProvinceAttractionResponse> getAllAttractions() {
        log.info("获取所有省份景点");
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findAll();
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 根据省份获取景点
    public List<ProvinceAttractionResponse> getAttractionsByProvince(String provinceName) {
        log.info("根据省份获取景点: {}", provinceName);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByProvinceName(provinceName);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 根据城市获取景点
    public List<ProvinceAttractionResponse> getAttractionsByCity(String city) {
        log.info("根据城市获取景点: {}", city);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByCity(city);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 根据类型获取景点
    public List<ProvinceAttractionResponse> getAttractionsByType(ProvinceAttraction.AttractionType attractionType) {
        log.info("根据类型获取景点: {}", attractionType);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByAttractionType(attractionType);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 获取热门景点
    public List<ProvinceAttractionResponse> getPopularAttractions() {
        log.info("获取热门景点");
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByIsPopularTrue();
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 获取推荐景点
    public List<ProvinceAttractionResponse> getRecommendedAttractions() {
        log.info("获取推荐景点");
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByIsRecommendedTrue();
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 获取省份热门景点
    public List<ProvinceAttractionResponse> getPopularAttractionsByProvince(String provinceName) {
        log.info("获取省份热门景点: {}", provinceName);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByProvinceNameAndIsPopularTrue(provinceName);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 获取省份推荐景点
    public List<ProvinceAttractionResponse> getRecommendedAttractionsByProvince(String provinceName) {
        log.info("获取省份推荐景点: {}", provinceName);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByProvinceNameAndIsRecommendedTrue(provinceName);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 获取省份特定类型景点
    public List<ProvinceAttractionResponse> getAttractionsByProvinceAndType(String provinceName, ProvinceAttraction.AttractionType attractionType) {
        log.info("获取省份特定类型景点: {} - {}", provinceName, attractionType);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByProvinceNameAndAttractionType(provinceName, attractionType);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 获取所有省份列表
    public List<String> getAllProvinces() {
        log.info("获取所有省份列表");
        return provinceAttractionRepository.findDistinctProvinceNames();
    }
    
    // 获取高评分景点
    public List<ProvinceAttractionResponse> getHighRatedAttractions(BigDecimal minRating) {
        log.info("获取高评分景点: {}", minRating);
        List<ProvinceAttraction> attractions = provinceAttractionRepository.findByRatingGreaterThanEqualOrderByRatingDesc(minRating);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 转换方法
    private ProvinceAttractionResponse convertToResponse(ProvinceAttraction attraction) {
        return ProvinceAttractionResponse.builder()
                .id(attraction.getId())
                .provinceName(attraction.getProvinceName())
                .attractionName(attraction.getAttractionName())
                .description(attraction.getDescription())
                .imageUrl(attraction.getImageUrl())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .address(attraction.getAddress())
                .city(attraction.getCity())
                .attractionType(attraction.getAttractionType())
                .rating(attraction.getRating())
                .visitDuration(attraction.getVisitDuration())
                .ticketPrice(attraction.getTicketPrice())
                .openingHours(attraction.getOpeningHours())
                .phone(attraction.getPhone())
                .website(attraction.getWebsite())
                .bestSeason(attraction.getBestSeason())
                .difficultyLevel(attraction.getDifficultyLevel())
                .isPopular(attraction.getIsPopular())
                .isRecommended(attraction.getIsRecommended())
                .build();
    }
}

