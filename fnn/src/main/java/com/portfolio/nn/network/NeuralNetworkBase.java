package com.portfolio.nn.network;

import java.io.IOException;

/**
 * Base interface for all neural network implementations.
 * Provides common methods for training, prediction, and serialization.
 */
public interface NeuralNetworkBase {

    /**
     * Forward pass through the network.
     * 
     * @param input Input data
     * @return Network output
     */
    double[] forward(double[] input);

    /**
     * Make a prediction on input data.
     * 
     * @param input Input data
     * @return Predicted class index
     */
    int predict(double[] input);

    /**
     * Train the network on given data.
     * 
     * @param x            Training inputs
     * @param y            Training targets
     * @param learningRate Learning rate
     * @param epochs       Number of training epochs
     */
    void train(double[][] x, double[][] y, double learningRate, int epochs);

    /**
     * Evaluate network performance on test data.
     * 
     * @param testX Test inputs
     * @param testY Test targets
     * @return Accuracy score
     */
    double evaluate(double[][] testX, double[][] testY);

    /**
     * Save the trained model to disk with timestamp.
     * 
     * @param modelName Name of the model to save
     * @throws IOException If file operations fail
     */
    void save(String modelName) throws IOException;

    /**
     * Load the most recent model from disk.
     * 
     * @param modelName Name of the model to load
     * @return Loaded neural network instance
     * @throws IOException If model file not found or loading fails
     */
    NeuralNetworkBase load(String modelName) throws IOException;

    /**
     * Load the specified model from disk.
     * 
     * @param modelName Name of the model to load
     * @param filename  Name of the file to load
     * @return Loaded neural network instance
     * @throws IOException If model file not found or loading fails
     */
    NeuralNetworkBase load(String modelName, String filename) throws IOException;
}