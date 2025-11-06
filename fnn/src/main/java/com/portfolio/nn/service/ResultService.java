package com.portfolio.nn.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.portfolio.nn.model.Result;
import com.portfolio.nn.repo.ResultRepo;

public class ResultService {
  
  @Autowired
  private ResultRepo repo;

  public Optional<Result> getResultById(String id){
    return repo.findById(id);
  }

}
