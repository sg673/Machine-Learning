package com.portfolio.nn.network.layers;

import com.portfolio.nn.network.activation.ActivationFunction;

public class ConvolutionalLayer extends LayerBase {
  private double[][][] filters; // [filterCount][filterHeight][filterWidth]
  private int filterSize;
  private int stride;
  private int padding;

  public ConvolutionalLayer(int inputWidth, int inputHeight, int inputDepth,
      int filterCount, int filterSize, int stride, int padding,
      ActivationFunction activationFunction) {
    
    super();
    this.inputWidth = inputWidth;
    this.inputHeight = inputHeight;
    this.inputDepth = inputDepth;
    this.filterSize = filterSize;
    this.stride = stride;
    this.padding = padding;
    this.activFunc = activationFunction;

    // Calculate output dimensions
    this.outputWidth = (inputWidth - filterSize + 2 * padding) / stride + 1;
    this.outputHeight = (inputHeight - filterSize + 2 * padding) / stride + 1;
    this.size = outputWidth * outputHeight * filterCount;

    // Initialize filters
    this.filters = new double[filterCount][filterSize][filterSize];
    this.biases = new double[filterCount];
    for (int f = 0; f < filters.length; f++) {
      for (int i = 0; i < filterSize; i++) {
        for (int j = 0; j < filterSize; j++) {
          filters[f][i][j] = Math.random() * 0.1 - 0.05;
        }
      }
      biases[f] = 0.0;
    }
  }

  public double[] forward(double[][][] input) {
    double[] output = new double[size];
    int outputIndex = 0;

    for (int f = 0; f < filters.length; f++) {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          double sum = convolve(input, f, x * stride, y * stride) + biases[f];
          output[outputIndex++] = activFunc.activate(sum);
        }
      }
    }
    return output;
  }

  // Calculates dot product of inputs over specific window of inputs
  private double convolve(double[][][] input, int filterIndex, int startX, int startY) {
    double sum = 0.0;
    for (int fy = 0; fy < filterSize; fy++) {
      for (int fx = 0; fx < filterSize; fx++) {
        int inputY = startY + fy - padding;
        int inputX = startX + fx - padding;

        if (inputY >= 0 && inputY < inputHeight && inputX >= 0 && inputX < inputWidth) {
          for (int c = 0; c < inputDepth; c++) {
            sum += input[c][inputY][inputX] * filters[filterIndex][fy][fx];
          }
        }
      }
    }
    return sum;
  }

}
