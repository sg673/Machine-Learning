package com.portfolio.nn.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class ResultController {

    @GetMapping("/results")
    public ResponseEntity<Object> getResults() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "List of results"));
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<Object> getResultById(@PathVariable("id") String resultId) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "id", resultId,
                "modelId", "tmp",
                "sessionId", "tmp",
                "finalAccuracy", 0,
                "finalLoss", 0,
                "trainingTime", 0,
                "epochs", 0,
                "completedAt", "2025-09-08T15:04:41.677Z"));
    }
}
