package com.travel.repository;

import com.travel.entity.BaiduMapMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BaiduMapMarkerRepository extends JpaRepository<BaiduMapMarker, Long> {
    
    List<BaiduMapMarker> findByType(BaiduMapMarker.MarkerType type);
    
    List<BaiduMapMarker> findByCity(String city);
    
    List<BaiduMapMarker> findByProvince(String province);
    
    List<BaiduMapMarker> findByIsPopularTrue();
    
    List<BaiduMapMarker> findByIsRecommendedTrue();
    
    List<BaiduMapMarker> findByIsFeaturedTrue();
    
    @Query("SELECT m FROM BaiduMapMarker m WHERE m.latitude BETWEEN :minLat AND :maxLat AND m.longitude BETWEEN :minLng AND :maxLng")
    List<BaiduMapMarker> findByBounds(@Param("minLat") BigDecimal minLat, 
                                    @Param("maxLat") BigDecimal maxLat,
                                    @Param("minLng") BigDecimal minLng, 
                                    @Param("maxLng") BigDecimal maxLng);
    
    @Query("SELECT m FROM BaiduMapMarker m WHERE m.type = :type AND m.city = :city")
    List<BaiduMapMarker> findByTypeAndCity(@Param("type") BaiduMapMarker.MarkerType type, @Param("city") String city);
    
    @Query("SELECT m FROM BaiduMapMarker m WHERE m.rating >= :minRating ORDER BY m.rating DESC")
    List<BaiduMapMarker> findByRatingGreaterThanEqualOrderByRatingDesc(@Param("minRating") BigDecimal minRating);
    
    @Query("SELECT m FROM BaiduMapMarker m WHERE m.name LIKE %:keyword% OR m.description LIKE %:keyword%")
    List<BaiduMapMarker> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT m FROM BaiduMapMarker m WHERE m.baiduUid = :baiduUid")
    BaiduMapMarker findByBaiduUid(@Param("baiduUid") String baiduUid);
    
    @Query("SELECT m FROM BaiduMapMarker m WHERE m.baiduPoiId = :baiduPoiId")
    BaiduMapMarker findByBaiduPoiId(@Param("baiduPoiId") String baiduPoiId);
}

