package com.portfolio.nn.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class TrainingController {

    @PostMapping("/training/start")
    public ResponseEntity<Object> postStartTraining() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "jobId", "12345",
                "status", "started"));
    }

    @GetMapping("/training/{sessionId}/status")
    public ResponseEntity<Object> getStatusById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "jobId", id,
                "status", "in progress",
                "epoch", 5,
                "batch", 44,
                "progress", 45.5,
                "accuracy", 92.3));
    }

    @PostMapping("/training/{sessionId}/stop")
    public ResponseEntity<Object> stopTrainingById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                "Training job " + id + " stopped");
    }
}
