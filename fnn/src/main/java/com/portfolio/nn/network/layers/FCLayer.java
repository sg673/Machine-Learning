package com.portfolio.nn.network.layers;

import com.portfolio.nn.network.activation.ActivationFunction;

public class FCLayer extends LayerBase {
  private double[][] weights;
  private int inputSize;

  public FCLayer(int outputSize,
      ActivationFunction activationFunction) {
    super();
    this.outputDepth = outputSize;
    this.outputWidth = 1;
    this.outputHeight = 1;

    this.activFunc = activationFunction;
  }

  @Override
  protected void computeOutputShape() {
    this.inputSize = inputWidth * inputHeight * inputDepth;
    this.weights = new double[outputDepth][inputSize];
    this.biases = new double[outputDepth];

    // Initialize weights
    for (int i = 0; i < outputDepth; i++) {
      for (int j = 0; j < inputSize; j++) {
        weights[i][j] = Math.random() * 0.1 - 0.05;
      }
      biases[i] = 0.0;
    }
  }

  @Override
  public double[][][] forward(double[][][] input) {
    this.lastInput = input;
    // Inputs need to be flattened so they're not 3d
    double[] flatInput = flatten(input);
    double[][][] output = new double[1][1][outputDepth];

    for (int i = 0; i < outputDepth; i++) {
      double sum = biases[i];
      for (int j = 0; j < inputSize; j++) {
        sum += weights[i][j] * flatInput[j];
      }
      output[0][0][i] = activFunc.activate(sum);
    }
    this.lastOutput = output;
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

  @Override
  public double[][][] backward(double[][][] gradient, double learningRate) {
    double[] flatInput = flatten(lastInput);
    double[][][] inputGradient = new double[inputDepth][inputHeight][inputWidth];

    for (int i = 0; i < outputDepth; i++) {
      double delta = gradient[0][0][i] * activFunc.derivative(lastOutput[0][0][i]);
      biases[i] -= learningRate * delta;

      for (int j = 0; j < inputSize; j++) {
        weights[i][j] -= learningRate * delta * flatInput[j];
        // Convert flat index back to 3D coordinates
        int c = j / (inputWidth * inputHeight);
        int remaining = j % (inputWidth * inputHeight);
        int y = remaining / inputWidth;
        int x = remaining % inputWidth;

        inputGradient[c][y][x] += weights[i][j] * delta;
      }
    }
    return inputGradient;
  }

}
