package com.portfolio.nn.network;

import java.util.Optional;

import com.portfolio.nn.network.layers.LayerBase;
import com.portfolio.nn.util.DataUtils;

public class ConvolutionalNetwork implements NeuralNetworkBase {

  // Head of linked list
  // Should usually be an input layer
  private Optional<LayerBase> head;

  public ConvolutionalNetwork() {
    this.head = Optional.empty();
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
    while (current.next.isPresent()){
      current = current.next.get();
      input3D = convertTo3D(output);
      output = current.forward(input3D);
    }
    return output;

  }

  @Override
  public void train(double[][] x, double[][] y, double learningRate, int epochs) {

  }

  @Override
  public double evaluate(double[][] testX, double[][] testY) {
    int correct = 0;
    for(int i = 0; i < testX.length;i++){
      int prediction = predict(testX[i]);
      int actual = DataUtils.getMaxIndex(testY[i]);
      if(prediction == actual){
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
