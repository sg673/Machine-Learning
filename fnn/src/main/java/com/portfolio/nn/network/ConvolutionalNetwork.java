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
      layer.inputDepth = 1;
      layer.inputHeight = 28;
      layer.inputWidth = 28;
      layer.updateOutputShape();
      head = Optional.of(layer);
    } else {
      LayerBase current = head.get();
      while (current.next.isPresent()) {
        current = current.next.get();
      }
      current.setNext(layer);
      layer.setPrev(current);

      layer.inputWidth = current.outputWidth;
      layer.inputHeight = current.outputHeight;
      layer.inputDepth = current.getOutputDepth();

      layer.updateOutputShape();
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
    double[][][] input3D = convertTo3D(input,current.inputDepth, current.inputHeight, current.inputWidth);
    double[] output = current.forward(input3D);
    while (current.next.isPresent()) {
      current = current.next.get();
      input3D = convertTo3D(output,current.inputDepth, current.inputHeight, current.inputWidth);
      output = current.forward(input3D);
    }
    return output;

  }

  @Override
  public void train(double[][] x, double[][] y, double learningRate, int epochs) {
    for (int epoch = 0; epoch < epochs; epoch++) {
      for (int i = 0; i < x.length; i++) {
        if( i % 100 == 0){
          System.out.print("\r Epoch:"+epoch+" i:"+i);
        }
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
}
