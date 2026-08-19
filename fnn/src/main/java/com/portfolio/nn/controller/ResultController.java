package com.portfolio.nn.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.nn.model.Result;
import com.portfolio.nn.service.ResultService;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1")
public class ResultController {

  @Autowired
  private ResultService resultService;

  @GetMapping("/results")
  public ResponseEntity<Object> getResults() {
    List<Result> results = resultService.getAllResults();
    return ResponseEntity.ok().body(results);
  }

  @GetMapping("/results/{id}")
  public ResponseEntity<Object> getResultById(@PathVariable("id") String resultId) {

    Optional<Result> result = resultService.getResultById(resultId);
    if (result.isPresent()) {
      return ResponseEntity.status(HttpStatus.OK).body(result.get());
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/results/{id}")
  public ResponseEntity<Object> deleteResultById(@PathVariable("id") String resultId) {
    boolean deleted = resultService.deleteById(resultId);
    if (deleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
