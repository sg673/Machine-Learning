package com.portfolio.nn.network;

/**
 * Base interface for all neural network implementations.
 * Provides common methods for training, prediction, and serialization.
 */
public interface NeuralNetworkBase {
    
    /**
     * Forward pass through the network.
     * @param input Input data
     * @return Network output
     */
    double[] forward(double[] input);
    
    /**
     * Make a prediction on input data.
     * @param input Input data
     * @return Predicted class index
     */
    int predict(double[] input);
    
    /**
     * Train the network on given data.
     * @param x Training inputs
     * @param y Training targets
     * @param learningRate Learning rate
     * @param epochs Number of training epochs
     */
    void train(double[][] x, double[][] y, double learningRate, int epochs);
    
    /**
     * Evaluate network performance on test data.
     * @param testX Test inputs
     * @param testY Test targets
     * @return Accuracy score
     */
    double evaluate(double[][] testX, double[][] testY);
}