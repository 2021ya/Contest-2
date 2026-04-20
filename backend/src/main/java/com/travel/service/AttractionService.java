package com.travel.service;

import com.travel.dto.AttractionResponse;
import com.travel.entity.Attraction;
import com.travel.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionService {
    
    private final AttractionRepository attractionRepository;
    private final JdbcTemplate jdbcTemplate;
    
    public List<AttractionResponse> getAllAttractions() {
        log.info("获取所有景点");
        List<Attraction> attractions = attractionRepository.findAll();
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<AttractionResponse> getRecommendedAttractions() {
        log.info("获取推荐景点");
        try {
            // 首先尝试从attraction_recommendations表获取数据
            String sql = "SELECT * FROM attraction_recommendations WHERE rating >= 4.5 ORDER BY rating DESC LIMIT 20";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            
            if (!results.isEmpty()) {
                return results.stream()
                        .map(this::convertMapToResponse)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("从attraction_recommendations表获取数据失败，尝试从attractions表获取: {}", e.getMessage());
        }
        
        // 如果attraction_recommendations表不存在或为空，从attractions表获取高评分景点
        List<Attraction> attractions = attractionRepository.findAll()
                .stream()
                .filter(attraction -> attraction.getRating() != null && attraction.getRating() >= 4.0)
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(20)
                .collect(Collectors.toList());
        
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<AttractionResponse> getAttractionsByType(Attraction.AttractionType type) {
        log.info("根据类型获取景点: {}", type);
        List<Attraction> attractions = attractionRepository.findByTypeOrderByRating(type);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<AttractionResponse> getNearbyAttractions(Double latitude, Double longitude, Double radiusKm) {
        log.info("获取附近景点: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        List<Attraction> attractions = attractionRepository.findNearbyAttractions(latitude, longitude, radiusKm);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<AttractionResponse> searchAttractions(String keyword) {
        log.info("搜索景点: {}", keyword);
        List<Attraction> attractions = attractionRepository.searchByNameOrDescription(keyword);
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public AttractionResponse getAttractionById(Long id) {
        log.info("获取景点详情: {}", id);
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("景点不存在"));
        return convertToResponse(attraction);
    }
    
    public List<AttractionResponse> getTopRatedAttractions(Integer limit) {
        log.info("获取高评分景点: limit={}", limit);
        List<Attraction> attractions = attractionRepository.findAll()
                .stream()
                .filter(attraction -> attraction.getRating() != null)
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<AttractionResponse> getFreeAttractions() {
        log.info("获取免费景点");
        List<Attraction> attractions = attractionRepository.findAll()
                .stream()
                .filter(attraction -> attraction.getTicketPrice() == null || attraction.getTicketPrice() == 0)
                .sorted((a, b) -> {
                    if (a.getRating() == null && b.getRating() == null) return 0;
                    if (a.getRating() == null) return 1;
                    if (b.getRating() == null) return -1;
                    return Double.compare(b.getRating(), a.getRating());
                })
                .collect(Collectors.toList());
        
        return attractions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private AttractionResponse convertToResponse(Attraction attraction) {
        AttractionResponse response = new AttractionResponse();
        response.setId(attraction.getId());
        response.setName(attraction.getName());
        response.setDescription(attraction.getDescription());
        response.setLatitude(attraction.getLatitude());
        response.setLongitude(attraction.getLongitude());
        response.setAddress(attraction.getAddress());
        response.setType(attraction.getType());
        response.setRating(attraction.getRating());
        response.setVisitDuration(attraction.getVisitDuration());
        response.setTicketPrice(attraction.getTicketPrice());
        response.setOpeningHours(attraction.getOpeningHours());
        response.setPhone(attraction.getPhone());
        response.setWebsite(attraction.getWebsite());
        response.setImageUrl(attraction.getImageUrl());
        response.setDestinationId(attraction.getDestination() != null ? attraction.getDestination().getId() : null);
        response.setCreatedAt(attraction.getCreatedAt());
        response.setUpdatedAt(attraction.getUpdatedAt());
        return response;
    }
    
    private AttractionResponse convertMapToResponse(Map<String, Object> map) {
        AttractionResponse response = new AttractionResponse();
        response.setId(((Number) map.get("id")).longValue());
        response.setName(map.get("name") != null ? (String) map.get("name") : "");
        response.setDescription(map.get("description") != null ? (String) map.get("description") : "");
        response.setLatitude(((Number) map.get("latitude")).doubleValue());
        response.setLongitude(((Number) map.get("longitude")).doubleValue());
        response.setAddress(map.get("address") != null ? (String) map.get("address") : "");
        response.setType(Attraction.AttractionType.valueOf((String) map.get("type")));
        response.setRating(map.get("rating") != null ? ((Number) map.get("rating")).doubleValue() : null);
        response.setVisitDuration(map.get("visit_duration") != null ? ((Number) map.get("visit_duration")).intValue() : null);
        response.setTicketPrice(map.get("ticket_price") != null ? ((Number) map.get("ticket_price")).doubleValue() : null);
        response.setOpeningHours(map.get("opening_hours") != null ? (String) map.get("opening_hours") : "");
        response.setPhone(map.get("phone") != null ? (String) map.get("phone") : "");
        response.setWebsite(map.get("website") != null ? (String) map.get("website") : "");
        response.setImageUrl(map.get("image_url") != null ? (String) map.get("image_url") : "");
        response.setDestinationId(null); // 景点推荐表中的数据没有关联的destination
        return response;
    }
}
