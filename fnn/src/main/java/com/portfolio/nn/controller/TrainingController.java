package com.portfolio.nn.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.model.TrainingSession;
import com.portfolio.nn.model.modelModel;
import com.portfolio.nn.service.TrainingService;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class TrainingController {

  @Autowired
  private TrainingService trainingService;

  @PostMapping(value = "/training/start", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> postStartTraining(@RequestBody modelModel model) {
    try {
      String sessionId = trainingService.startTraining(model);
      return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
          "sessionId", sessionId,
          "status", SessionStatus.INITIALIZED));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  // Retraining models that already exist
  @PostMapping(value = "/training/start/{modelId}")
  public ResponseEntity<Object> postReTrain(@PathVariable("modelId") String modelId){
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/training/{id}/status")
  public ResponseEntity<Object> getStatusById(@PathVariable("id") String sessionId) {
    TrainingSession session = trainingService.getSession(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }
    double progress = session.getTotalEpochs() > 0
        ? ((double) (session.getCurrentEpoch() - 1) * session.getTotalBatches()
            + session.getCurrentBatch()) /
            (session.getTotalEpochs() * session.getTotalBatches()) * 100
        : 0;
    return ResponseEntity.status(HttpStatus.OK).body(Map.of(
        "sessionId", sessionId,
        "status", session.getStatus(),
        "epoch", session.getCurrentEpoch(),
        "totalEpochs", session.getTotalEpochs(),
        "batch", session.getCurrentBatch(),
        "totalBatches", session.getTotalBatches(),
        "progress", Math.round(progress * 100.0) / 100.0,

        "accuracy", session.getAccuracy()));
  }

  @PostMapping("/training/{id}/stop")
  public ResponseEntity<Object> stopTrainingById(@PathVariable("id") String sessionId) {
    TrainingSession session = trainingService.getSession(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    if (session.getStatus() == SessionStatus.COMPLETED ||
        session.getStatus() == SessionStatus.STOPPED) {
      return ResponseEntity.badRequest().body(Map.of(
          "error", "Session already finished",
          "status", session.getStatus()));
    }
    boolean stopped = trainingService.stopSession(sessionId);
    if (!stopped) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.status(HttpStatus.OK).body(Map.of(
        "sessionId", sessionId,
        "status", SessionStatus.STOPPED));

  }

  @PostMapping("/training/cnn/{id}/start")
  public ResponseEntity<Object> startTrainingById(@PathVariable("id") String modelId){
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/training/cnn/{id}/status")
  public ResponseEntity<Object> getCNNStatusById(@PathVariable("id") String sessionId) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PostMapping("/training/cnn/{id}/stop")
  public ResponseEntity<Object> stopCNNTrainingById(@PathVariable("id") String sessionId) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
