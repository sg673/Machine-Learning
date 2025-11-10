package com.portfolio.nn.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.nn.model.modelModel;
import com.portfolio.nn.model.Model;
import com.portfolio.nn.service.ModelService;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class ModelController {

  @Autowired
  private ModelService modelService;

  @GetMapping("/models")
  public ResponseEntity<Object> getModels() {
    return ResponseEntity.status(HttpStatus.OK).body(
        modelService.getAllModels());
  }

  // Deprecated 
  @PostMapping(value = "/models", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> postModels(@RequestBody modelModel model) {
    return ResponseEntity.ok(model);
  }

  @GetMapping("/models/{id}")
  public ResponseEntity<Object> getModelById(@PathVariable("id") String id) {
    Optional<Model> model = modelService.getModelById(id);
    if (model.isPresent()) {
      return ResponseEntity.status(HttpStatus.OK).body(model.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/models/{id}")
  public ResponseEntity<Object> deleteModelById(@PathVariable("id") String id) {
    boolean deleted = modelService.deleteModelById(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
