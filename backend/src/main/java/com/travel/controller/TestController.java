package com.travel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/attractions")
    public List<Map<String, Object>> testAttractions() {
        String sql = "SELECT id, name, type, rating FROM attractions LIMIT 5";
        return jdbcTemplate.queryForList(sql);
    }
    
    @GetMapping("/count")
    public Map<String, Object> testCount() {
        String sql = "SELECT COUNT(*) as count FROM attractions";
        return jdbcTemplate.queryForMap(sql);
    }
}
