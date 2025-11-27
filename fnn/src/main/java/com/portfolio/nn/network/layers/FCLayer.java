package com.portfolio.nn.network.layers;

import com.portfolio.nn.network.activation.ActivationFunction;

public class FCLayer extends LayerBase {
  private double[][] weights;
  private int inputSize;

  public FCLayer(int inputWidth, int inputHeight, int inputDepth, int outputSize, 
                 ActivationFunction activationFunction) {
    super();
    this.inputWidth = inputWidth;
    this.inputHeight = inputHeight;
    this.inputDepth = inputDepth;
    this.inputSize = inputWidth * inputHeight * inputDepth;
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

  public double[] forward(double[][][] input) {
    // Inputs need to be flattened so they're not 3d
    double[] flatInput = flatten(input);
    double[] output = new double[size];
    
    for (int i = 0; i < size; i++) {
      double sum = biases[i];
      for (int j = 0; j < inputSize; j++) {
        sum += weights[i][j] * flatInput[j];
      }
      output[i] = activFunc.activate(sum);
    }
    return output;
  }

  private double[] flatten(double[][][] input) {
    double[] flat = new double[inputSize];
    int index = 0;
    for (int c = 0; c < inputDepth; c++) {
      for (int y = 0; y < inputHeight; y++) {
        for (int x = 0; x < inputWidth; x++) {
          flat[index++] = input[c][y][x];
        }
      }
    }
    return flat;
  }

}
