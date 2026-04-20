package com.travel.repository;

import com.travel.entity.ProvinceAttraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceAttractionRepository extends JpaRepository<ProvinceAttraction, Long> {
    
    List<ProvinceAttraction> findByProvinceName(String provinceName);
    
    List<ProvinceAttraction> findByCity(String city);
    
    List<ProvinceAttraction> findByAttractionType(ProvinceAttraction.AttractionType attractionType);
    
    List<ProvinceAttraction> findByIsPopularTrue();
    
    List<ProvinceAttraction> findByIsRecommendedTrue();
    
    @Query("SELECT p FROM ProvinceAttraction p WHERE p.provinceName = :provinceName AND p.isPopular = true")
    List<ProvinceAttraction> findByProvinceNameAndIsPopularTrue(@Param("provinceName") String provinceName);
    
    @Query("SELECT p FROM ProvinceAttraction p WHERE p.provinceName = :provinceName AND p.isRecommended = true")
    List<ProvinceAttraction> findByProvinceNameAndIsRecommendedTrue(@Param("provinceName") String provinceName);
    
    @Query("SELECT p FROM ProvinceAttraction p WHERE p.provinceName = :provinceName AND p.attractionType = :attractionType")
    List<ProvinceAttraction> findByProvinceNameAndAttractionType(@Param("provinceName") String provinceName, 
                                                                @Param("attractionType") ProvinceAttraction.AttractionType attractionType);
    
    @Query("SELECT DISTINCT p.provinceName FROM ProvinceAttraction p ORDER BY p.provinceName")
    List<String> findDistinctProvinceNames();
    
    @Query("SELECT p FROM ProvinceAttraction p WHERE p.rating >= :minRating ORDER BY p.rating DESC")
    List<ProvinceAttraction> findByRatingGreaterThanEqualOrderByRatingDesc(@Param("minRating") java.math.BigDecimal minRating);
}

