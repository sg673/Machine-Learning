package com.portfolio.nn.network.layers;

import java.util.stream.IntStream;

/**
 * Pooling Layer implementation for spatial downsampling and feature reduction.
 * Reduces spatial dimensions while preserving important features through
 * max or average pooling operations. Helps reduce overfitting and computational
 * cost.
 */
public class PoolingLayer extends LayerBase {
  /**
   * Pooling operation types.
   * MAX preserves dominant features, AVERAGE captures general patterns.
   */
  public enum PoolingType {
    MAX, AVERAGE
  }

  /** Size of square pooling window */
  private int poolSize;
  /** Step size for pooling window movement */
  private int stride;
  /** Type of pooling operation to perform */
  private PoolingType poolingType;

  /** Cached indices of max elements for backpropagation */
  private transient int[][][] maxIndices;
  /** Cached output for efficient memory usage */
  private transient double[][][] outputCache;

  /**
   * Creates a pooling layer with specified parameters.
   * 
   * @param poolSize    size of square pooling window
   * @param stride      step size for pooling
   * @param poolingType MAX or AVERAGE pooling
   */
  public PoolingLayer(
      int poolSize, int stride, PoolingType poolingType) {

    super();
    this.poolSize = poolSize;
    this.stride = stride;
    this.poolingType = poolingType;

  }

  @Override
  protected void computeOutputShape() {
    this.outputWidth = (inputWidth - poolSize) / stride + 1;
    this.outputHeight = (inputHeight - poolSize) / stride + 1;
    this.outputDepth = inputDepth;

    this.outputCache = new double[outputDepth][outputHeight][outputWidth];
    if (poolingType == PoolingType.MAX) {
      this.maxIndices = new int[outputDepth][outputHeight][outputWidth];
    }
  }

  @Override
  public double[][][] forward(double[][][] input) {
    this.lastInput = input;
    IntStream.range(0, inputDepth).parallel().forEach(c -> {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          int startX = x * stride;
          int startY = y * stride;

          if (poolingType == PoolingType.MAX) {
            double maxVal = Double.NEGATIVE_INFINITY;
            int maxIdx = 0;

            for (int py = 0; py < poolSize; py++) {
              for (int px = 0; px < poolSize; px++) {
                double val = input[c][startY + py][startX + px];
                if (val > maxVal) {
                  maxVal = val;
                  maxIdx = py * poolSize + px; // Flatten 2D index to 1D
                }
              }
            }

            outputCache[c][y][x] = maxVal;
            maxIndices[c][y][x] = maxIdx;
          } else {
            double sum = 0.0;
            for (int py = 0; py < poolSize; py++) {
              for (int px = 0; px < poolSize; px++) {
                sum += input[c][startY + py][startX + px];
              }
            }
            outputCache[c][y][x] = sum / (poolSize * poolSize);
          }
        }
      }
    });

    this.lastOutput = outputCache;
    return outputCache;
  }

  @Override
  public double[][][] backward(double[][][] gradient, double learningRate) {
    double[][][] inputGradient = new double[inputDepth][inputHeight][inputWidth];

    IntStream.range(0, inputDepth).parallel().forEach(c -> {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          double grad = gradient[c][y][x];
          int startX = x * stride;
          int startY = y * stride;

          if (poolingType == PoolingType.MAX) {
            // Use cached max index - eliminates redundant search
            int maxIdx = maxIndices[c][y][x];
            int maxPy = maxIdx / poolSize;
            int maxPx = maxIdx % poolSize;
            inputGradient[c][startY + maxPy][startX + maxPx] += grad;
          } else {
            double avgGrad = grad / (poolSize * poolSize);
            for (int py = 0; py < poolSize; py++) {
              for (int px = 0; px < poolSize; px++) {
                inputGradient[c][startY + py][startX + px] += avgGrad;
              }
            }
          }
        }
      }
    });

    return inputGradient;
  }
}
