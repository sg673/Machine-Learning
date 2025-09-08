package com.portfolio.nn.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class HomeController {

    @GetMapping("test")
    public String getMethodName() {
        return "Hello World!";
    }

    // Convert to dynamic, peristent checks
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Object> getDashboardStats() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "models", 12,
                "jobs", 5,
                "successRate", 87.8,
                "avgAccuracy", 99.8));
    }

}
