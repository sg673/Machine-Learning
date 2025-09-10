package com.portfolio.nn.model;

import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.network.FeedForwardNetwork;

public class TrainingSession {
    private final String sessionId;
    private transient final FeedForwardNetwork network;
    private SessionStatus status;
    private int currentEpoch;
    private final int totalEpochs;
    private int currentBatch;
    private final int totalBatches;
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
}
