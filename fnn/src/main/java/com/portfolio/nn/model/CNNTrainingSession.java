package com.portfolio.nn.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.network.ConvolutionalNetwork;
import com.portfolio.nn.repo.ResultRepo;

public class CNNTrainingSession {
  private final String sessionId;
  private SessionStatus status;
  private int currentEpoch;
  private final int totalEpochs;
  private int currentBatch;
  private int totalBatches;
  private double accuracy;
  private final String modelId;
  private final String trainingData;
  private final ConvolutionalNetwork network;
  private final long startTime = System.currentTimeMillis();
  private boolean isRunning;

  public CNNTrainingSession(ConvolutionalNetwork model, CNNTrainingParameters params,
      String modelId, String sessionId) {
    initialiseDefaultAttributes();
    this.sessionId = sessionId;
    this.modelId = modelId;
    this.network = model;
    this.totalEpochs = params.epochs;
    this.totalBatches = (int) Math.ceil((double) model.getTrainingData().getTrainingSize() / params.batchSize);
    this.trainingData = model.getTrainingData().getName();

  }

  private void initialiseDefaultAttributes() {
    this.status = SessionStatus.INITIALIZED;
    this.currentEpoch = 0;
    this.currentBatch = 0;
    this.accuracy = 0.0;
    this.isRunning = false;
  }

  /**
   * Save the trained model to Repository with timestamp.
   * 
   * @param repo - Repo to save the result to
   */

  public boolean Save(ResultRepo repo) {
    return Save(repo,null);
  }

  public boolean Save(ResultRepo repo, String errorMessage){
    if (this.isRunning)
      return false;
    Result result = new Result();
    result.setModelId(this.modelId);
    result.setSessionId(this.sessionId);
    result.setFinalAccuracy(this.accuracy);
    result.setTrainingTime(System.currentTimeMillis() - startTime);
    result.setEpochs(this.totalEpochs);
    result.setTotalBatches(this.totalBatches);
    result.setCompletedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    result.setFinalStatus(status);
    result.setErrorMessage(errorMessage);
    repo.save(result);
    return true;
  }

  public ConvolutionalNetwork getNetwork() {
    return this.network;
  }
  
  // Getters
  public int getCurrentBatch() {
    return this.currentBatch;
  }

  public int getCurrentEpoch() {
    return this.currentEpoch;
  }

  public String getSessionId() {
    return sessionId;
  }

  public SessionStatus getStatus() {
    return status;
  }

  public int getTotalEpochs() {
    return totalEpochs;
  }

  public int getTotalBatches() {
    return totalBatches;
  }

  public double getAccuracy() {
    return accuracy;
  }

  public String getModelId() {
    return modelId;
  }

  public String getTrainingData() {
    return trainingData;
  }

  public long getStartTime() {
    return startTime;
  }

  public boolean isRunning() {
    return isRunning;
  }

  // Setters (only for non-final fields)
  public void setStatus(SessionStatus status) {
    this.status = status;
  }

  public void setCurrentEpoch(int currentEpoch) {
    this.currentEpoch = currentEpoch;
  }

  public void setCurrentBatch(int currentBatch) {
    this.currentBatch = currentBatch;
  }

  public void setTotalBatches(int totalBatches) {
    this.totalBatches = totalBatches;
  }

  public void setAccuracy(double accuracy) {
    this.accuracy = accuracy;
  }

  public void setRunning(boolean running) {
    this.isRunning = running;
  }

}
