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

  public void setLossFunction(LossFunction lossFunction){
    this.lossFunction = lossFunction;
  }

  public boolean addLayer(LayerBase layer) {
    if (head.isEmpty()) {
      head = Optional.of(layer);
      return true;
    } else {
      LayerBase current = head.get();
      while (current.next.isPresent()) {
        current = current.next.get();
      }
      current.setNext(layer);
      layer.setPrev(current);
      return true;
    }
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
    double[][][] input3D = convertTo3D(input);
    LayerBase current = head.get();
    double[] output = current.forward(input3D);
    while (current.next.isPresent()) {
      current = current.next.get();
      input3D = convertTo3D(output);
      output = current.forward(input3D);
    }
    return output;

  }

  @Override
  public void train(double[][] x, double[][] y, double learningRate, int epochs) {
    for (int epoch = 0; epoch < epochs; epoch++) {
      for (int i = 0; i < x.length; i++) {
        double[] output = forward(x[i]);
        double[] gradient = lossFunction.calculateGradient(output, y[i]);

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

  private double[][][] convertTo3D(double[] input) {
    // For MNIST: 784 -> 28x28x1
    int size = (int) Math.sqrt(input.length);
    double[][][] result = new double[1][size][size];

    for (int i = 0; i < input.length; i++) {
      int row = i / size;
      int col = i % size;
      result[0][row][col] = input[i];
    }

    return result;
  }
}
