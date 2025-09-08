package com.portfolio.nn.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class ModelController {

    // Should return a list of all model names + their ids
    @GetMapping("/models")
    public ResponseEntity<Object> getModels() {
        return ResponseEntity.status(HttpStatus.OK).body(
                "12");
    }

    // May not be needed
    @PostMapping("/models")
    public ResponseEntity<Object> postModels() {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                "Model created");
    }

    //
    @GetMapping("/models/{id}")
    public ResponseEntity<Object> getModelById(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(
                "id", id,
                "name", "MNIST Classifier",
                "type", "FFN",
                "architecture", Map.of(
                        "inputSize", 784,
                        "hiddenLayers", new int[] { 128, 64 },
                        "outputSize", 10,
                        "activationFunction", "RELU")));
    }

    @DeleteMapping("/models/{id}")
    public ResponseEntity<Object> deleteModelById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                "Model with id " + id + " deleted");
    }
}
