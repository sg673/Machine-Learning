package com.portfolio.nn.network.layers;

import java.util.stream.IntStream;

public class PoolingLayer extends LayerBase {
  // max preserves dominant features,
  // average general patterns
  public enum PoolingType {
    MAX, AVERAGE
  }

  private int poolSize;
  private int stride;
  private PoolingType poolingType;

  private int[][][] maxIndices; // [channel][y][x] -> flattened index of max element
  private double[][][] outputCache;

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
