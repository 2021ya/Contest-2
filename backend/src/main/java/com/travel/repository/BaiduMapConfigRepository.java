package com.travel.repository;

import com.travel.entity.BaiduMapConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaiduMapConfigRepository extends JpaRepository<BaiduMapConfig, Long> {
    
    Optional<BaiduMapConfig> findByConfigKeyAndIsActiveTrue(String configKey);
    
    Optional<BaiduMapConfig> findByConfigKey(String configKey);
}

