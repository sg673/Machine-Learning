package com.portfolio.nn.controller;

import java.util.Optional;

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

import com.portfolio.nn.model.CNNModel;
import com.portfolio.nn.model.CNNTrainingParameters;
import com.portfolio.nn.model.CNNTrainingSession;
import com.portfolio.nn.service.CNNModelService;
import com.portfolio.nn.service.CNNTrainingService;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1/training/cnn")
public class CNNTrainingController {

  @Autowired
  private CNNModelService modelService;
  @Autowired
  private CNNTrainingService trainingService;

  /**
   * Initiates training for a CNN model by its ID.
   * 
   * <p>
   * Validates the model exists, then starts an asynchronous training session
   * with the provided parameters. Returns a unique session ID for monitoring
   * progress.
   * </p>
   * 
   * @param modelId the unique identifier of the CNN model to train
   * @param params  training configuration including epochs, batch size, and
   *                learning rate
   * @return ResponseEntity containing the session ID (201 Created) or error
   *         message (404 Not Found)
   */
  @PostMapping(name = "/{id}/start", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> startTrainingById(@PathVariable("id") String modelId,
      @RequestBody CNNTrainingParameters params) {

    Optional<CNNModel> model = modelService.getModelById(modelId);
    if (model.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model with ID " + modelId + " not found.");
    }
    String sessionId = trainingService.startTraining(model.get(), params);
    return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);

  }

  @GetMapping("/{id}/status")
  public ResponseEntity<Object> getCNNStatusById(@PathVariable("id") String sessionId) {
    CNNTrainingSession session = trainingService.getSession(sessionId);
    if (session == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session with ID " + sessionId + " not found.");
    }
    return ResponseEntity.ok(session);
  }

  @PostMapping("/{id}/stop")
  public ResponseEntity<Object> stopCNNTrainingById(@PathVariable("id") String sessionId) {
    boolean stopped = trainingService.stopSession(sessionId);
    if (!stopped) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session with ID " + sessionId + " not found.");
    }
    return ResponseEntity.ok("Session with ID " + sessionId + " stopped.");
  }
}
