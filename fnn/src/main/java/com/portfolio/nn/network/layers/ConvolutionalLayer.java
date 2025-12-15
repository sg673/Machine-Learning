package com.portfolio.nn.network.layers;

import java.util.stream.IntStream;

import com.portfolio.nn.network.activation.ActivationFunction;

public class ConvolutionalLayer extends LayerBase {
  private double[][][] filters; // [filterCount][filterHeight][filterWidth]
  private int filterSize;
  private int stride;
  private int padding;

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
  }

  @Override
  public double[][][] forward(double[][][] input) {
    this.lastInput = input;
    double[][] inputCols = im2col(input);

    double[][] filterMatrix = new double[outputDepth][filterSize * filterSize * inputDepth];
    IntStream.range(0,filters.length).parallel().forEach(f -> {
      int idx = 0;
      for (int fy = 0; fy < filterSize; fy++) {
        for (int fx = 0; fx < filterSize; fx++) {
          filterMatrix[f][idx++] = filters[f][fy][fx];
        }
      }
    });
    

    // Matrix mult
    double[][] result = matrixMultiply(filterMatrix, transpose(inputCols));

    double[][][] output = new double[outputDepth][outputHeight][outputWidth];
    IntStream.range(0, outputDepth).parallel().forEach(f ->{
      for (int i = 0; i < outputHeight * outputWidth; i++) {
        int y = i / outputWidth;
        int x = i % outputWidth;
        output[f][y][x] = activFunc.activate(result[f][i] + biases[f]);
      }
    });

    this.lastOutput = output;
    return output;
  }

  @Override
  public double[][][] backward(double[][][] gradient, double learningRate) {
    double[][][] inputGradient = new double[inputDepth][inputHeight][inputWidth];

    for (int f = 0; f < filters.length; f++) {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          double delta = gradient[f][y][x] * activFunc.derivative(lastOutput[f][y][x]);
          biases[f] -= learningRate * delta;

          // Updates filter weights
          for (int fy = 0; fy < filterSize; fy++) {
            for (int fx = 0; fx < filterSize; fx++) {
              int inputY = y * stride + fy - padding;
              int inputX = x * stride + fx - padding;

              if (inputY >= 0 && inputY < inputHeight && inputX >= 0 && inputX < inputWidth) {
                for (int c = 0; c < inputDepth; c++) {
                  inputGradient[c][inputY][inputX] += filters[f][fy][fx] * delta;
                  filters[f][fy][fx] -= learningRate * lastInput[c][inputY][inputX] * delta;
                }
              }
            }
          }
        }
      }
    }
    return inputGradient;
  }

  private double[][] im2col(double[][][] input) {
    int patchCount = outputHeight * outputWidth;
    int patchSize = filterSize * filterSize * inputDepth;
    double[][] patches = new double[patchCount][patchSize];

    int patchIdx = 0;
    for (int y = 0; y < outputHeight; y++) {
      for (int x = 0; x < outputWidth; x++) {
        int idx = 0;
        for (int c = 0; c < inputDepth; c++) {
          for (int fy = 0; fy < filterSize; fy++) {
            for (int fx = 0; fx < filterSize; fx++) {
              int inputY = y * stride + fy - padding;
              int inputX = x * stride + fx - padding;
              patches[patchIdx][idx++] = (inputY >= 0 && inputY < inputHeight &&
                  inputX >= 0 && inputX < inputWidth) ? input[c][inputY][inputX] : 0.0;
            }
          }
        }
        patchIdx++;
      }
    }
    return patches;
  }

  private double[][] matrixMultiply(double[][] a, double[][] b){
    int aRows = a.length;
    int aCols = a[0].length;
    int bCols = b[0].length;
    double[][] result = new double[aRows][bCols];

    for (int i = 0; i < aRows; i++) {
      for (int j = 0; j < bCols; j++) {
        for (int k = 0; k < aCols; k++) {
          result[i][j] += a[i][k] * b[k][j];
        }
      }
    }
    return result;
  }

  private double[][] transpose(double[][] matrix) {
    double[][] result = new double[matrix[0].length][matrix.length];
    for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[0].length; j++) {
            result[j][i] = matrix[i][j];
        }
    }
    return result;
}

}
