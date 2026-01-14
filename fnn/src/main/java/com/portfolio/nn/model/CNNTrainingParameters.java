package com.portfolio.nn.model;

/**
 * Configuration parameters for CNN model training.
 * 
 * <p>
 * Contains hyperparameters that control the training process including
 * iteration count, batch processing size, and gradient descent step size.
 * </p>
 */
public class CNNTrainingParameters {
  /** Number of complete passes through the training dataset */
  public int epochs;
  /** Number of training samples processed before updating model weights */
  public int batchSize;
  /** Step size for gradient descent optimization algorithm */
  public double learningRate;

}
