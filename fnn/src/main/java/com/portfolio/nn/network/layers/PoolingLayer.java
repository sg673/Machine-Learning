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

  @Override
  public double[] forward(double[][][] input) {
    this.lastInput = input;
    double[] output = new double[size];
    int outputIndex = 0;

    for (int c = 0; c < inputDepth; c++) {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          output[outputIndex++] = pool(input[c], x * stride, y * stride);
        }
      }
    }
    this.lastOutput = output;
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

  @Override
  public double[] backward(double[] gradient, double learningRate) {
    double[] inputGradient = new double[inputWidth * inputHeight * inputDepth];
    int gradIndex = 0;

    for (int c = 0; c < inputDepth; c++) {
      for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
          double grad = gradient[gradIndex++];

          if (poolingType == PoolingType.MAX) {
            int maxY = y * stride, maxX = x * stride;
            double maxVal = lastInput[c][maxY][maxX];

            for (int py = 0; py < poolSize; py++) {
              for (int px = 0; px < poolSize; px++) {
                if (lastInput[c][y * stride + py][x * stride + px] > maxVal) {
                  maxY = y * stride + py;
                  maxX = x * stride + px;
                  maxVal = lastInput[c][maxY][maxX];
                }
              }
            }
            inputGradient[c * inputWidth * inputHeight + maxY * inputWidth + maxX] += grad;
          } else {
            double avgGrad = grad / (poolSize * poolSize);
            for (int py = 0; py < poolSize;py++){
              for (int px = 0; px < poolSize;px++){
                int idx = c * inputWidth * inputHeight + (y*stride+py) * inputWidth + (x*stride+px);
                inputGradient[idx] += avgGrad;
              }
            }
          }
        }
      }
    }
    return inputGradient;
  }
}
