package com.portfolio.nn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "results")
public class Result {

  @Id
  @Column(name = "result_id")
  private String resultId;

  @Column(name = "model_id")
  private String modelId;

  @Column(name = "session_id")
  private String sessionId;

  @Column(name = "final_accuracy")
  private double finalAccuracy;

  @Column(name = "final_loss")
  private double finalLoss;

  @Column(name = "training_time_ms")
  private long trainingTime; // in milliseconds

  private int epochs;
  private int batches;

  @Column(name = "completed_at")
  private String completedAt; // ISO 8601 format

  // Getters and Setters
  public String getId() {
    return resultId;
  }

  public String getModelId() {
    return modelId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public double getFinalAccuracy() {
    return finalAccuracy;
  }

  public double getFinalLoss() {
    return finalLoss;
  }

  public long getTrainingTime() {
    return trainingTime;
  }

  public int getEpochs() {
    return epochs;
  }
  public int getBatchSize(){
    return batches;
  }

  public String getCompletedAt() {
    return completedAt;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public void setFinalAccuracy(double finalAccuracy) {
    this.finalAccuracy = finalAccuracy;
  }

  public void setFinalLoss(double finalLoss) {
    this.finalLoss = finalLoss;
  }

  public void setTrainingTime(long trainingTime) {
    this.trainingTime = trainingTime;
  }

  public void setEpochs(int epochs) {
    this.epochs = epochs;
  }

  public void setTotalBatches(int batches){
    this.batches = batches;
  }

  public void setCompletedAt(String completedAt) {
    this.completedAt = completedAt;
  }

}
