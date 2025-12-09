package com.portfolio.nn.network;

import java.util.Optional;

import com.portfolio.nn.network.layers.LayerBase;
import com.portfolio.nn.network.loss.CategoricalCrossEntropy;
import com.portfolio.nn.network.loss.LossFunction;
import com.portfolio.nn.util.DataUtils;

public class ConvolutionalNetwork implements NeuralNetworkBase {

  // Head of linked list
  // Should usually be an input layer
  private Optional<LayerBase> head;

  private LossFunction lossFunction;

  public ConvolutionalNetwork() {
    this.head = Optional.empty();
    this.lossFunction = new CategoricalCrossEntropy();
  }

  public void setLossFunction(LossFunction lossFunction) {
    this.lossFunction = lossFunction;
  }

  public ConvolutionalNetwork addLayer(LayerBase layer) {
    if (head.isEmpty()) {
      // TODO replace temp MNIST input with generic
      layer.setInputShape(28, 28, 1);
      head = Optional.of(layer);
    } else {
      LayerBase current = head.get();
      while (current.next.isPresent()) {
        current = current.next.get();
      }
      current.setNext(layer);
      layer.setPrev(current);

      int[] outputShape = current.getOutputShape();
      layer.setInputShape(outputShape[0], outputShape[1], outputShape[2]);
    }
    return this;
  }

  @Override
  public int predict(double[] input) {
    double[] output = forward(input);
    return DataUtils.getMaxIndex(output);
  }

  @Override
  public double[] forward(double[] input) {
    if (head.isEmpty()) {
      throw new Error("No layers defined");
    }
    LayerBase current = head.get();
    // TODO replace temp MNIST input with generic
    double[][][] tensor = convertTo3D(input, 1, 28, 28);
    while (current != null) {
      tensor = current.forward(tensor);
      current = current.next.orElse(null);
    }
    return flatten(tensor);
  }

  @Override
  public void train(double[][] x, double[][] y, double learningRate, int epochs) {
    for (int epoch = 0; epoch < epochs; epoch++) {
      for (int i = 0; i < x.length; i++) {
        if (i % 100 == 0) {
          System.out.print("\r Epoch:" + epoch + " i:" + i);
        }
        double[] output = forward(x[i]);
        double[][][] gradient = convertTo3D(lossFunction.calculateGradient(output, y[i]), 1, 1, output.length);

        if (head.isPresent()) {
          LayerBase current = head.get();
          while (current.next.isPresent()) {
            current = current.next.get();
          }
          while (current != null) {
            gradient = current.backward(gradient, learningRate);
            current = current.prev.orElse(null);
          }
        }
      }
    }
  }

  @Override
  public double evaluate(double[][] testX, double[][] testY) {
    int correct = 0;
    for (int i = 0; i < testX.length; i++) {
      int prediction = predict(testX[i]);
      int actual = DataUtils.getMaxIndex(testY[i]);
      if (prediction == actual) {
        correct++;
      }
    }
    return (double) correct / testX.length;
  }

  private double[][][] convertTo3D(double[] input, int depth, int height, int width) {
    double[][][] result = new double[depth][height][width];

    for (int i = 0; i < input.length && i < depth * height * width; i++) {
      int d = i / (height * width);
      int h = (i % (height * width)) / width;
      int w = i % width;
      result[d][h][w] = input[i];
    }

    return result;
  }

  private double[] flatten(double[][][] tensor) {
    int size = tensor.length * tensor[0].length * tensor[0][0].length;
    double[] result = new double[size];
    int index = 0;
    for (int d = 0; d < tensor.length; d++) {
      for (int h = 0; h < tensor[0].length; h++) {
        for (int w = 0; w < tensor[0][0].length; w++) {
          result[index++] = tensor[d][h][w];
        }
      }
    }
    return result;
  }
}
