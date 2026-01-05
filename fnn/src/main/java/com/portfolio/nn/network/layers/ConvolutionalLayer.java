package com.portfolio.nn.network.layers;

import java.util.stream.IntStream;

import com.portfolio.nn.network.activation.ActivationFunction;

/**
 * Convolutional Layer implementation for feature extraction from spatial data.
 * Applies learnable filters across input using convolution operation to detect
 * local patterns and features. Supports configurable filter size, stride, and
 * padding.
 */
public class ConvolutionalLayer extends LayerBase {
  /** 3D filters for convolution [filterCount][filterHeight][filterWidth] */
  private double[][][] filters;
  /** Size of square convolution filters */
  private int filterSize;
  /** Step size for filter movement */
  private int stride;
  /** Zero-padding around input borders */
  private int padding;

  /** Pre-computed filter matrix for optimization */
  private transient double[][] filterMatrix;
  /** Flattened output cache for parallel processing */
  private transient double[] flatOutput;

  /**
   * Creates a convolutional layer with specified parameters.
   * 
   * @param filterCount        number of filters (output channels)
   * @param filterSize         size of square filters
   * @param stride             step size for convolution
   * @param padding            zero-padding around input
   * @param activationFunction activation function for outputs
   */
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

  /**
   * Computes output dimensions based on convolution parameters.
   * Formula: (input_size - filter_size + 2*padding) / stride + 1
   */
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
