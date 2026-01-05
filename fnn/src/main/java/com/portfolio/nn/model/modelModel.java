package com.portfolio.nn.model;

import com.portfolio.nn.network.activation.ActivationFunction;
/*
 * This class is for structuring user inputs.
 * It should not be used to save models or be persisted in any way
 * For that, look at Model
 * TODO - change naming to fix this ambiguity
 */


public class modelModel {
    private String modelName;
    private String trainingData;
    private int epochs;
    private int batchSize;
    private double learningRate;
    private String layers;
    private ActivationFunction activationFunction;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(String trainingData) {
        this.trainingData = trainingData;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public String getLayers() {
        return layers;
    }

    public void setLayers(String layers) {
        this.layers = layers;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }
}
