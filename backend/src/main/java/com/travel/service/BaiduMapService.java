package com.travel.service;

import com.travel.dto.BaiduMapMarkerRequest;
import com.travel.dto.BaiduMapMarkerResponse;
import com.travel.entity.BaiduMapConfig;
import com.travel.entity.BaiduMapMarker;
import com.travel.repository.BaiduMapConfigRepository;
import com.travel.repository.BaiduMapMarkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaiduMapService {
    
    private final BaiduMapMarkerRepository baiduMapMarkerRepository;
    private final BaiduMapConfigRepository baiduMapConfigRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    // 获取所有百度地图标记
    public List<BaiduMapMarkerResponse> getAllMarkers() {
        log.info("获取所有百度地图标记");
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findAll();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 根据类型获取标记
    public List<BaiduMapMarkerResponse> getMarkersByType(BaiduMapMarker.MarkerType type) {
        log.info("根据类型获取百度地图标记: {}", type);
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByType(type);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 根据城市获取标记
    public List<BaiduMapMarkerResponse> getMarkersByCity(String city) {
        log.info("根据城市获取百度地图标记: {}", city);
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByCity(city);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取热门标记
    public List<BaiduMapMarkerResponse> getPopularMarkers() {
        log.info("获取热门百度地图标记");
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByIsPopularTrue();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取推荐标记
    public List<BaiduMapMarkerResponse> getRecommendedMarkers() {
        log.info("获取推荐百度地图标记");
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByIsRecommendedTrue();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取精选标记
    public List<BaiduMapMarkerResponse> getFeaturedMarkers() {
        log.info("获取精选百度地图标记");
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByIsFeaturedTrue();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 根据边界获取标记
    public List<BaiduMapMarkerResponse> getMarkersByBounds(BigDecimal minLat, BigDecimal maxLat, 
                                                           BigDecimal minLng, BigDecimal maxLng) {
        log.info("根据边界获取百度地图标记: lat[{}, {}], lng[{}, {}]", minLat, maxLat, minLng, maxLng);
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByBounds(minLat, maxLat, minLng, maxLng);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 搜索标记
    public List<BaiduMapMarkerResponse> searchMarkers(String keyword) {
        log.info("搜索百度地图标记: {}", keyword);
        List<BaiduMapMarker> markers = baiduMapMarkerRepository.findByKeyword(keyword);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 创建标记
    public BaiduMapMarkerResponse createMarker(BaiduMapMarkerRequest request) {
        log.info("创建百度地图标记: {}", request.getName());
        
        BaiduMapMarker marker = BaiduMapMarker.builder()
                .name(request.getName())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .country(request.getCountry())
                .type(request.getType())
                .rating(request.getRating())
                .priceLevel(request.getPriceLevel())
                .openingHours(request.getOpeningHours())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .imageUrl(request.getImageUrl())
                .iconType(request.getIconType())
                .color(request.getColor())
                .size(request.getSize())
                .isPopular(request.getIsPopular())
                .isRecommended(request.getIsRecommended())
                .isFeatured(request.getIsFeatured())
                .baiduUid(request.getBaiduUid())
                .baiduPoiId(request.getBaiduPoiId())
                .build();
        
        BaiduMapMarker savedMarker = baiduMapMarkerRepository.save(marker);
        return convertToMarkerResponse(savedMarker);
    }
    
    // 从百度地图API获取地点信息
    public BaiduMapMarkerResponse getPlaceFromBaiduApi(String query, String city) {
        log.info("从百度地图API获取地点信息: {}, 城市: {}", query, city);
        
        try {
            String apiKey = getApiKey();
            String url = UriComponentsBuilder.fromHttpUrl("https://api.map.baidu.com/place/v2/search")
                    .queryParam("query", query)
                    .queryParam("region", city)
                    .queryParam("output", "json")
                    .queryParam("ak", apiKey)
                    .build()
                    .toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // 这里需要解析百度地图API的响应
                // 由于百度地图API返回的是JSON格式，需要解析并转换为我们的实体
                log.info("成功从百度地图API获取数据");
                // TODO: 解析API响应并创建标记
            }
            
        } catch (Exception e) {
            log.error("从百度地图API获取地点信息失败", e);
        }
        
        return null;
    }
    
    // 获取API密钥
    private String getApiKey() {
        Optional<BaiduMapConfig> config = baiduMapConfigRepository.findByConfigKeyAndIsActiveTrue("api_key");
        return config.map(BaiduMapConfig::getConfigValue).orElse("olsASap8GgVEbXt884QT2r4bhWpulMRE");
    }
    
    // 转换方法
    private BaiduMapMarkerResponse convertToMarkerResponse(BaiduMapMarker marker) {
        return BaiduMapMarkerResponse.builder()
                .id(marker.getId())
                .name(marker.getName())
                .description(marker.getDescription())
                .latitude(marker.getLatitude())
                .longitude(marker.getLongitude())
                .address(marker.getAddress())
                .city(marker.getCity())
                .province(marker.getProvince())
                .country(marker.getCountry())
                .type(marker.getType())
                .rating(marker.getRating())
                .priceLevel(marker.getPriceLevel())
                .openingHours(marker.getOpeningHours())
                .phone(marker.getPhone())
                .website(marker.getWebsite())
                .imageUrl(marker.getImageUrl())
                .iconType(marker.getIconType())
                .color(marker.getColor())
                .size(marker.getSize())
                .isPopular(marker.getIsPopular())
                .isRecommended(marker.getIsRecommended())
                .isFeatured(marker.getIsFeatured())
                .visitCount(marker.getVisitCount())
                .likeCount(marker.getLikeCount())
                .commentCount(marker.getCommentCount())
                .baiduUid(marker.getBaiduUid())
                .baiduPoiId(marker.getBaiduPoiId())
                .build();
    }
}

