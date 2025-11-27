package com.portfolio.nn.network.layers;

import com.portfolio.nn.network.activation.ActivationFunction;

public class FCLayer extends LayerBase {
  private double[][] weights;
  private int inputSize;

  public FCLayer(int inputSize, int outputSize, ActivationFunction activationFunction) {
    super();
    this.inputSize = inputSize;
    this.size = outputSize;
    this.activFunc = activationFunction;

    this.weights = new double[outputSize][inputSize];
    this.biases = new double[outputSize];
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < inputSize; j++) {
        weights[i][j] = Math.random() * 0.1 - 0.05;
      }
      biases[i] = 0.0;
    }
  }

  public double[] forward(double[] input) {
    double[] output = new double[size];

    for (int i = 0; i < size; i++) {
      double sum = biases[i];
      for (int j = 0; j < inputSize; j++) {
        sum += weights[i][j] * input[j];
      }
      output[i] = activFunc.activate(sum);
    }
    return output;
  }

}
