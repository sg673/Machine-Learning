package com.portfolio.nn.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.network.FeedForwardNetwork;

public class TrainingSession {
  private final String sessionId;
  private final FeedForwardNetwork network;
  private SessionStatus status;
  private int currentEpoch;
  private final int totalEpochs;
  private int currentBatch;
  private int totalBatches;
  private double accuracy;

  private boolean isRunning;

  public TrainingSession(String sessionId, FeedForwardNetwork network, int totalEpochs, int totalBatches) {
    this.sessionId = sessionId;
    this.network = network;
    this.totalEpochs = totalEpochs;
    this.totalBatches = totalBatches;
    this.status = SessionStatus.INITIALIZED;
    this.currentEpoch = 0;
    this.currentBatch = 0;
    this.accuracy = 0.0;
    this.isRunning = false;
  }

  /**
   * Save the trained model to disk with timestamp.
   * 
   * @param modelName Name of the model to save
   * @throws IOException If file operations fail
   */
  public void Save(String modelName) throws IOException {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"));
    Path dir = Paths.get("savedModels", modelName);
    Files.createDirectories(dir);
    Gson gson = new Gson();
    String json = gson.toJson(this);
    String filename = dir.resolve(modelName + "-" + timestamp + ".json").toString();
    Files.write(Paths.get(filename), json.getBytes());
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
}
