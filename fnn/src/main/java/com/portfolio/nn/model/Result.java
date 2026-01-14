package com.portfolio.nn.model;

import java.util.UUID;

import com.portfolio.nn.constants.SessionStatus;

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

  @Column(name = "training_time_ms")
  private long trainingTime; // in milliseconds

  private int epochs;
  private int batches;

  @Column(name = "completed_at")
  private String completedAt; // ISO 8601 format

  private SessionStatus finalStatus;

  @Column(name="error_message")
  private String errorMessage;

  public Result() {
    this.resultId = UUID.randomUUID().toString();
  }

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

  public long getTrainingTime() {
    return trainingTime;
  }

  public int getEpochs() {
    return epochs;
  }

  public int getBatchSize() {
    return batches;
  }

  public String getCompletedAt() {
    return completedAt;
  }

  public SessionStatus getFinalSessionStatus() {
    return finalStatus;
  }
  public String getErrorMessage() {
    return errorMessage;
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

  public void setTrainingTime(long trainingTime) {
    this.trainingTime = trainingTime;
  }

  public void setEpochs(int epochs) {
    this.epochs = epochs;
  }

  public void setTotalBatches(int batches) {
    this.batches = batches;
  }

  public void setCompletedAt(String completedAt) {
    this.completedAt = completedAt;
  }

  public void setFinalStatus(SessionStatus status) {
    this.finalStatus = status;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

}
