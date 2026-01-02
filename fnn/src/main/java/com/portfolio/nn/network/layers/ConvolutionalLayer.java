package com.portfolio.nn.network.layers;

import java.util.stream.IntStream;

import com.portfolio.nn.network.activation.ActivationFunction;

public class ConvolutionalLayer extends LayerBase {
  private double[][][] filters; // [filterCount][filterHeight][filterWidth]
  private int filterSize;
  private int stride;
  private int padding;

  private double[][] filterMatrix;
  private double[] flatOutput;

  public ConvolutionalLayer(
      int filterCount, int filterSize, int stride, int padding,
      ActivationFunction activationFunction) {

    super();
    this.filterSize = filterSize;
    this.stride = stride;
    this.padding = padding;
    this.activFunc = activationFunction;
    this.outputDepth = filterCount;

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

  @Override
  protected void computeOutputShape() {
    this.outputWidth = (inputWidth - filterSize + 2 * padding) / stride + 1;
    this.outputHeight = (inputHeight - filterSize + 2 * padding) / stride + 1;

    this.filterMatrix = new double[outputDepth][filterSize * filterSize * inputDepth];
    this.flatOutput = new double[outputDepth * outputHeight * outputWidth];
    
    // Pre-compute filter matrix once
    IntStream.range(0, filters.length).parallel().forEach(f -> {
      int idx = 0;
      for (int fy = 0; fy < filterSize; fy++) {
        for (int fx = 0; fx < filterSize; fx++) {
          filterMatrix[f][idx++] = filters[f][fy][fx];
        }
      }
    });
  }

  @Override
  public double[][][] forward(double[][][] input) {
    this.lastInput = input;
    int outputSize = outputHeight * outputWidth;
    
    IntStream.range(0, outputDepth).parallel().forEach(f -> {
      int baseIdx = f * outputSize;
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          double sum = biases[f];
          
          // Direct convolution computation
          for (int c = 0; c < inputDepth; c++) {
            for (int fy = 0; fy < filterSize; fy++) {
              for (int fx = 0; fx < filterSize; fx++) {
                int inputY = y * stride + fy - padding;
                int inputX = x * stride + fx - padding;
                
                if (inputY >= 0 && inputY < inputHeight && inputX >= 0 && inputX < inputWidth) {
                  sum += input[c][inputY][inputX] * filters[f][fy][fx];
                }
              }
            }
          }
          
          flatOutput[baseIdx + y * outputWidth + x] = activFunc.activate(sum);
        }
      }
    });

    // Convert flat output to 3D - optimized memory allocation
    double[][][] output = new double[outputDepth][outputHeight][outputWidth];
    IntStream.range(0, outputDepth).parallel().forEach(f -> {
      int baseIdx = f * outputSize;
      for (int y = 0; y < outputHeight; y++) {
        System.arraycopy(flatOutput, baseIdx + y * outputWidth, output[f][y], 0, outputWidth);
      }
    });

    this.lastOutput = output;
    return output;
  }

  @Override
  public double[][][] backward(double[][][] gradient, double learningRate) {
    double[][][] inputGradient = new double[inputDepth][inputHeight][inputWidth];

    IntStream.range(0, filters.length).parallel().forEach(f -> {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          double delta = gradient[f][y][x] * activFunc.derivative(lastOutput[f][y][x]);
          synchronized (this) {
            biases[f] -= learningRate * delta;
          }
          // Updates filter weights
          for (int fy = 0; fy < filterSize; fy++) {
            for (int fx = 0; fx < filterSize; fx++) {
              int inputY = y * stride + fy - padding;
              int inputX = x * stride + fx - padding;

              if (inputY >= 0 && inputY < inputHeight && inputX >= 0 && inputX < inputWidth) {
                for (int c = 0; c < inputDepth; c++) {
                  synchronized (inputGradient) {
                    inputGradient[c][inputY][inputX] += filters[f][fy][fx] * delta;
                  }
                  synchronized (filters[f]) {
                    filters[f][fy][fx] -= learningRate * lastInput[c][inputY][inputX] * delta;
                  }
                }
              }
            }
          }
        }
      }
    });
    return inputGradient;
  }
}
