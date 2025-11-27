package com.portfolio.nn.network.layers;

public class PoolingLayer extends LayerBase {
  // max preserves dominant features,
  // average general patterns
  public enum PoolingType {
    MAX, AVERAGE
  }

  private int poolSize;
  private int stride;
  private PoolingType poolingType;

  public PoolingLayer(int inputWidth, int inputHeight, int inputDepth,
      int poolSize, int stride, PoolingType poolingType) {

    super();
    this.inputWidth = inputWidth;
    this.inputHeight = inputHeight;
    this.inputDepth = inputDepth;
    this.poolSize = poolSize;
    this.stride = stride;
    this.poolingType = poolingType;

    // Calculate output dimensions
    this.outputWidth = (inputWidth - poolSize) / stride + 1;
    this.outputHeight = (inputHeight - poolSize) / stride + 1;
    this.size = outputWidth * outputHeight * inputDepth;
  }

  public double[] forward(double[][][] input) {
    double[] output = new double[size];
    int outputIndex = 0;

    for (int c = 0; c < inputDepth; c++) {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          output[outputIndex++] = pool(input[c], x * stride, y * stride);
        }
      }
    }
    return output;
  }

  // Downsamples network (Reduces computation required)
  private double pool(double[][] channelInput, int startX, int startY) {
    double result = poolingType == PoolingType.MAX ? Double.NEGATIVE_INFINITY : 0.0;

    for (int py = 0; py < poolSize; py++) {
      for (int px = 0; px < poolSize; px++) {
        double value = channelInput[startY + py][startX + px];
        if (poolingType == PoolingType.MAX) {
          result = Math.max(result, value);
        } else {
          result += value;
        }
      }
    }

    return poolingType == PoolingType.AVERAGE ? result / (poolSize * poolSize) : result;
  }
}
