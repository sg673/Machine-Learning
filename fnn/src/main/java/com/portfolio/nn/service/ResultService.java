package com.portfolio.nn.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.nn.model.Result;
import com.portfolio.nn.repo.ResultRepo;

@Service
public class ResultService {

  @Autowired
  private ResultRepo repo;

  public Optional<Result> getResultById(String id) {
    return repo.findById(id);
  }

  public List<Result> getAllResults() {
    return repo.findAll();
  }

  public boolean deleteById(String id) {
    if (repo.existsById(id)) {
      repo.deleteById(id);
      return true;
    }
    return false;
  }

}
