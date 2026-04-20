package com.travel.controller;

import com.travel.dto.TravelPlanRequest;
import com.travel.dto.TravelPlanResponse;
import com.travel.service.AiTravelPlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/travel-plans")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TravelPlanController {
    
    private final AiTravelPlanningService aiTravelPlanningService;
    
    @PostMapping("/generate")
    public ResponseEntity<TravelPlanResponse> generateTravelPlan(
            @Valid @RequestBody TravelPlanRequest request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        
        log.info("收到生成旅游计划请求: {}", request.getTitle());
        
        // 从请求头中获取用户ID，如果没有则默认为1
        Long userId = getUserIdFromRequest(httpRequest);
        
        TravelPlanResponse response = aiTravelPlanningService.generateTravelPlan(request, userId);
        
        return ResponseEntity.ok(response);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 从请求头中获取用户ID
        String userIdHeader = request.getHeader("X-User-ID");
        log.info("请求头中的用户ID: {}", userIdHeader);
        
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                log.info("解析到的用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID: {}", userIdHeader);
            }
        }
        // 如果没有提供用户ID或解析失败，默认为1
        log.info("使用默认用户ID: 1");
        return 1L;
    }
    
    @GetMapping
    public ResponseEntity<List<TravelPlanResponse>> getUserTravelPlans(
            HttpServletRequest httpRequest,
            Authentication authentication) {
        log.info("获取用户旅游计划列表");
        
        // 从请求头中获取用户ID，如果没有则默认为1
        Long userId = getUserIdFromRequest(httpRequest);
        
        List<TravelPlanResponse> plans = aiTravelPlanningService.getUserTravelPlans(userId);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TravelPlanResponse> getTravelPlan(@PathVariable Long id) {
        log.info("获取旅游计划详情，ID: {}", id);
        
        try {
            TravelPlanResponse plan = aiTravelPlanningService.getTravelPlanById(id);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            log.error("获取旅游计划失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TravelPlanResponse> updateTravelPlan(
            @PathVariable Long id,
            @Valid @RequestBody TravelPlanRequest request) {
        log.info("更新旅游计划，ID: {}", id);
        
        try {
            TravelPlanResponse updatedPlan = aiTravelPlanningService.updateTravelPlan(id, request);
            return ResponseEntity.ok(updatedPlan);
        } catch (RuntimeException e) {
            log.error("更新旅游计划失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravelPlan(@PathVariable Long id) {
        log.info("删除旅游计划，ID: {}", id);
        
        try {
            aiTravelPlanningService.deleteTravelPlan(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("删除旅游计划失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}



























