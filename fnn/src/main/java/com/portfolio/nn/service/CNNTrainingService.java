package com.portfolio.nn.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.portfolio.nn.constants.DataSet;
import com.portfolio.nn.model.CNNModel;
import com.portfolio.nn.model.CNNTrainingParameters;
import com.portfolio.nn.model.CNNTrainingSession;
import com.portfolio.nn.network.ConvolutionalNetwork;


@Service
public class CNNTrainingService {

  private final Map<String, CNNTrainingSession> sessions = new ConcurrentHashMap<>();

  public String startTraining(CNNModel model, CNNTrainingParameters params){
    String sessionId = UUID.randomUUID().toString();
    DataSet dataSet;
    try{
      dataSet = DataSet.fromString(model.trainingData);
    } catch(IllegalArgumentException e){
      throw new RuntimeException("Invalid dataset specified: " + model.trainingData);
    }
    ConvolutionalNetwork network = new ConvolutionalNetwork(dataSet);
    CNNTrainingSession session = new CNNTrainingSession(network, params, model.modelId, sessionId);
    sessions.put(sessionId, session);
    
    new Thread(() ->{
      /**
       * Init Layers in model 
       * load dataset
       * start training
       * save on completion
       */
    });


    return sessionId;
  }
}
