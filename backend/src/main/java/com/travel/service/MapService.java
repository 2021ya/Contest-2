package com.travel.service;

import com.travel.dto.MapLayerResponse;
import com.travel.dto.MapMarkerResponse;
import com.travel.dto.MapRouteResponse;
import com.travel.entity.MapLayer;
import com.travel.entity.MapMarker;
import com.travel.entity.MapRoute;
import com.travel.repository.MapLayerRepository;
import com.travel.repository.MapMarkerRepository;
import com.travel.repository.MapRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {
    
    private final MapMarkerRepository mapMarkerRepository;
    private final MapLayerRepository mapLayerRepository;
    private final MapRouteRepository mapRouteRepository;
    
    // 获取所有地图标记
    public List<MapMarkerResponse> getAllMarkers() {
        log.info("获取所有地图标记");
        List<MapMarker> markers = mapMarkerRepository.findAll();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 根据类型获取标记
    public List<MapMarkerResponse> getMarkersByType(MapMarker.MarkerType type) {
        log.info("根据类型获取地图标记: {}", type);
        List<MapMarker> markers = mapMarkerRepository.findByType(type);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 根据城市获取标记
    public List<MapMarkerResponse> getMarkersByCity(String city) {
        log.info("根据城市获取地图标记: {}", city);
        List<MapMarker> markers = mapMarkerRepository.findByCity(city);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取热门标记
    public List<MapMarkerResponse> getPopularMarkers() {
        log.info("获取热门地图标记");
        List<MapMarker> markers = mapMarkerRepository.findByIsPopularTrue();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取推荐标记
    public List<MapMarkerResponse> getRecommendedMarkers() {
        log.info("获取推荐地图标记");
        List<MapMarker> markers = mapMarkerRepository.findByIsRecommendedTrue();
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 根据边界获取标记
    public List<MapMarkerResponse> getMarkersByBounds(BigDecimal minLat, BigDecimal maxLat, 
                                                     BigDecimal minLng, BigDecimal maxLng) {
        log.info("根据边界获取地图标记: lat[{}, {}], lng[{}, {}]", minLat, maxLat, minLng, maxLng);
        List<MapMarker> markers = mapMarkerRepository.findByBounds(minLat, maxLat, minLng, maxLng);
        return markers.stream()
                .map(this::convertToMarkerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取所有图层
    public List<MapLayerResponse> getAllLayers() {
        log.info("获取所有地图图层");
        List<MapLayer> layers = mapLayerRepository.findByIsVisibleTrue();
        return layers.stream()
                .map(this::convertToLayerResponse)
                .collect(Collectors.toList());
    }
    
    // 获取所有路线
    public List<MapRouteResponse> getAllRoutes() {
        log.info("获取所有地图路线");
        List<MapRoute> routes = mapRouteRepository.findAll();
        return routes.stream()
                .map(this::convertToRouteResponse)
                .collect(Collectors.toList());
    }
    
    // 根据路线类型获取路线
    public List<MapRouteResponse> getRoutesByType(MapRoute.RouteType routeType) {
        log.info("根据类型获取地图路线: {}", routeType);
        List<MapRoute> routes = mapRouteRepository.findByRouteType(routeType);
        return routes.stream()
                .map(this::convertToRouteResponse)
                .collect(Collectors.toList());
    }
    
    // 获取风景路线
    public List<MapRouteResponse> getScenicRoutes() {
        log.info("获取风景路线");
        List<MapRoute> routes = mapRouteRepository.findByIsScenicTrue();
        return routes.stream()
                .map(this::convertToRouteResponse)
                .collect(Collectors.toList());
    }
    
    // 转换方法
    private MapMarkerResponse convertToMarkerResponse(MapMarker marker) {
        return MapMarkerResponse.builder()
                .id(marker.getId())
                .name(marker.getName())
                .description(marker.getDescription())
                .latitude(marker.getLatitude())
                .longitude(marker.getLongitude())
                .address(marker.getAddress())
                .city(marker.getCity())
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
                .build();
    }
    
    private MapLayerResponse convertToLayerResponse(MapLayer layer) {
        return MapLayerResponse.builder()
                .id(layer.getId())
                .name(layer.getName())
                .displayName(layer.getDisplayName())
                .description(layer.getDescription())
                .layerType(layer.getLayerType())
                .isVisible(layer.getIsVisible())
                .isDefault(layer.getIsDefault())
                .zIndex(layer.getZIndex())
                .styleConfig(layer.getStyleConfig())
                .build();
    }
    
    private MapRouteResponse convertToRouteResponse(MapRoute route) {
        return MapRouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .description(route.getDescription())
                .startLatitude(route.getStartLatitude())
                .startLongitude(route.getStartLongitude())
                .endLatitude(route.getEndLatitude())
                .endLongitude(route.getEndLongitude())
                .routeType(route.getRouteType())
                .distanceKm(route.getDistanceKm())
                .durationMinutes(route.getDurationMinutes())
                .difficulty(route.getDifficulty())
                .isScenic(route.getIsScenic())
                .routeData(route.getRouteData())
                .build();
    }
}
