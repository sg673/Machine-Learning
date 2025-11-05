package com.portfolio.nn.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.network.FeedForwardNetwork;
import com.portfolio.nn.repo.ResultRepo;

public class TrainingSession {

  @Autowired
  private ResultRepo repo;

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
    
    this.sessionId = sessionId;
    this.network = network;
    this.totalEpochs = totalEpochs;
    this.totalBatches = totalBatches;
    this.status = SessionStatus.INITIALIZED;
    this.currentEpoch = 0;
    this.currentBatch = 0;
    this.accuracy = 0.0;
    this.isRunning = false;
    this.modelName = modelName;
    this.trainingData = trainingData;
  }

  /**
   * Save the trained model to disk with timestamp.
   * 
   * @param modelId
   */
  public void Save(String modelId){
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

  /**
   * Load the most recent model from disk.
   * 
   * @param modelName Name of the model to load
   * @return Loaded neural network instance
   * @throws IOException If model file not found or loading fails
   */
  public TrainingSession Load(String modelName) throws IOException {
    Path dir = Paths.get("savedModels", modelName);
    if (!Files.exists(dir)) {
      throw new IOException("Model directory not found: " + dir);
    }

    Path mostRecent = Files.list(dir)
        .filter(path -> path.toString().endsWith(".json"))
        .max((p1, p2) -> {
          try {
            return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
          } catch (IOException e) {
            return 0;
          }
        })
        .orElseThrow(() -> new IOException("No model files found in: " + dir));

    String json = new String(Files.readAllBytes(mostRecent));
    return new Gson().fromJson(json, TrainingSession.class);
  }

  /**
   * Load the specified model from disk.
   * 
   * @param modelName Name of the model to load
   * @param filename  Name of the file to load
   * @return Loaded neural network instance
   * @throws IOException If model file not found or loading fails
   */
  public TrainingSession Load(String modelName, String filename) throws IOException {
    Path dir = Paths.get("savedModels", modelName);
    if (!Files.exists(dir)) {
      throw new IOException("Model directory not found: " + dir);
    }
    try {
      String json = new String(Files.readAllBytes(Paths.get("savedModels", modelName, filename)));
      return new Gson().fromJson(json, TrainingSession.class);
    } catch (IOException e) {
      System.out.println(e);
      throw new IOException("Error loading model from file: " + filename, e);
    }
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
