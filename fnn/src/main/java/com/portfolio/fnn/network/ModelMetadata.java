package com.portfolio.fnn.network;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ModelMetadata {
    private int epochsTrained = 0;
    private double learningRate = 0.0;
    private double finalLoss = 0.0;
    private double testAccuracy = 0.0;
    private long trainingTimeMs = 0;
    private String trainingDate = "";

    public void setTrainingStart(double learningRate) {
        this.learningRate = learningRate;
        this.trainingDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public void incrementEpoch() {
        this.epochsTrained++;
    }

    public void setTrainingComplete(double finalLoss, long trainingTimeMs) {
        this.finalLoss = finalLoss;
        this.trainingTimeMs = trainingTimeMs;
    }

    public void setTestAccuracy(double testAccuracy) {
        this.testAccuracy = testAccuracy;
    }

    // Getters
    public int getEpochsTrained() { return epochsTrained; }
    public double getLearningRate() { return learningRate; }
    public double getFinalLoss() { return finalLoss; }
    public double getTestAccuracy() { return testAccuracy; }
    public long getTrainingTimeMs() { return trainingTimeMs; }
    public String getTrainingDate() { return trainingDate; }

    // Setters for loading
    public void setEpochsTrained(int epochsTrained) { this.epochsTrained = epochsTrained; }
    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
    public void setFinalLoss(double finalLoss) { this.finalLoss = finalLoss; }
    public void setTrainingTimeMs(long trainingTimeMs) { this.trainingTimeMs = trainingTimeMs; }
    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }
}