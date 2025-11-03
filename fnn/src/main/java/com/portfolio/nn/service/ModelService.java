package com.portfolio.nn.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.portfolio.nn.model.Model;
import com.portfolio.nn.repo.ModelRepo;

@Service
public class ModelService {

  @Autowired
  private ModelRepo modelRepo;

  private final Gson gson = new Gson();

  public List<Model> getAllModels(){
    return modelRepo.findAll();
  }

  public Optional<Model> getModelById(String id){
    return modelRepo.findById(id);
  }

  //Perform translation from 
  public Model saveModel(Model model){
    return modelRepo.save(model);
  }

  public Model saveModelWithWeights(Model model, double[][][] weights, double[][] biases){
    model.setWeights(gson.toJson(weights));
    model.setBiases(gson.toJson(biases));
    return modelRepo.save(model);
  }
  public void deleteModelById(String id){
    modelRepo.deleteById(id);
  }


  
}
