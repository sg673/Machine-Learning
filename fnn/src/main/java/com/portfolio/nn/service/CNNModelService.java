package com.portfolio.nn.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.nn.model.CNNModel;
import com.portfolio.nn.repo.CNNRepo;

@Service
public class CNNModelService {
  @Autowired
  private CNNRepo repo;

  public List<CNNModel> getAll(){
    return repo.findAll();
  }

  public Optional<CNNModel> getModelById(String id){
    return repo.findById(id);
  }

  public CNNModel saveModel(CNNModel model){
    return repo.save(model);
  }

  public boolean updateModel(String id, CNNModel model){
    Optional<CNNModel> existingModel = repo.findById(id);
    if(existingModel.isPresent()){
      repo.save(model);
      return true;
    } else {
      return false;
    }
  }

  public boolean deleteById(String id){
    Optional<CNNModel> model = repo.findById(id);
    if(model.isPresent()){
      repo.deleteById(id);
      return true;
    } else {
      return false;
    }
  }
}
