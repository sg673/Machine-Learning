package com.portfolio.nn.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.network.FeedForwardNetwork;
import com.portfolio.nn.repo.ResultRepo;

public class TrainingSession {

  private final String sessionId;
  private SessionStatus status;
  private int currentEpoch;
  private final int totalEpochs;
  private int currentBatch;
  private int totalBatches;
  private double accuracy;
  private final String modelName;
  @SuppressWarnings("unused")
  private final String trainingData;
  private final FeedForwardNetwork network;
  private final long startTime = System.currentTimeMillis();

  private boolean isRunning;

  public TrainingSession(String sessionId, FeedForwardNetwork network, int totalEpochs, int totalBatches,
      String modelName, String trainingData) {

    initialiseDefaultAttributes();
    this.sessionId = sessionId;
    this.network = network;
    this.totalEpochs = totalEpochs;
    this.totalBatches = totalBatches;
    this.modelName = modelName;
    this.trainingData = trainingData;
  }

  

  /**
   * Save the trained model to Repository with timestamp.
   * 
   * @param modelId
   */
  public void Save(String modelId, ResultRepo repo){
    if(this.isRunning) return;
    Result result = new Result();
    result.setModelId(modelId);
    result.setSessionId(modelId);
    result.setFinalAccuracy(this.accuracy);
    result.setTrainingTime(System.currentTimeMillis() - startTime);
    result.setEpochs(this.totalEpochs);
    result.setTotalBatches(this.totalBatches);
    result.setCompletedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    result.setFinalStatus(status);
    repo.save(result);
  }

  private void initialiseDefaultAttributes(){
    this.status = SessionStatus.INITIALIZED;
    this.currentEpoch = 0;
    this.currentBatch = 0;
    this.accuracy = 0.0;
    this.isRunning = false;
  }

  // Getters and setters
  public String getSessionId() {
    return sessionId;
  }

  public FeedForwardNetwork getNetwork() {
    return network;
  }

  public SessionStatus getStatus() {
    return status;
  }

  public void setStatus(SessionStatus status) {
    this.status = status;
  }

  public int getCurrentEpoch() {
    return currentEpoch;
  }

  public void setCurrentEpoch(int currentEpoch) {
    this.currentEpoch = currentEpoch;
  }

  public int getTotalEpochs() {
    return totalEpochs;
  }

  public int getCurrentBatch() {
    return currentBatch;
  }

  public void setCurrentBatch(int currentBatch) {
    this.currentBatch = currentBatch;
  }

  public int getTotalBatches() {
    return totalBatches;
  }

  public double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(double accuracy) {
    this.accuracy = accuracy;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void setRunning(boolean running) {
    isRunning = running;
  }

  public void setTotalBatches(int totalBatches) {
    this.totalBatches = totalBatches;
  }

  public String getModelName() {
    return modelName;
  }

}
