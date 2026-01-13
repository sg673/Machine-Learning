package com.portfolio.nn.controller;

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
@RequestMapping("/api/v1/training/cnn")
public class CNNTrainingController {
  @PostMapping("/{id}/start")
  public ResponseEntity<Object> startTrainingById(@PathVariable("id") String modelId){
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/{id}/status")
  public ResponseEntity<Object> getCNNStatusById(@PathVariable("id") String sessionId) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PostMapping("/{id}/stop")
  public ResponseEntity<Object> stopCNNTrainingById(@PathVariable("id") String sessionId) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
