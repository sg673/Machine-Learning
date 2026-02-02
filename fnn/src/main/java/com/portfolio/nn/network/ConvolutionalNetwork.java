package com.portfolio.nn.network;

import java.util.Optional;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.IntStream;

import com.portfolio.nn.constants.DataSet;
import com.portfolio.nn.model.CNNTrainingSession;
import com.portfolio.nn.network.layers.LayerBase;
import com.portfolio.nn.network.loss.CategoricalCrossEntropy;
import com.portfolio.nn.network.loss.LossFunction;
import com.portfolio.nn.util.DataUtils;

public class ConvolutionalNetwork implements NeuralNetworkBase {

  // Head of linked list
  // Should usually be an input layer
  private Optional<LayerBase> head;

  private Optional<LayerBase> tail;

  private LossFunction lossFunction;

  private DataSet trainingData;

  // TODO add session and update acc, batches, etc over time

  public ConvolutionalNetwork(DataSet trainingData) {
    this.head = Optional.empty();
    this.lossFunction = new CategoricalCrossEntropy();
    this.trainingData = trainingData;
  }

  public DataSet getTrainingData() {
    return trainingData;
  }

  public void setLossFunction(LossFunction lossFunction) {
    this.lossFunction = lossFunction;
  }

  public ConvolutionalNetwork addLayer(LayerBase layer) {
    if (head.isEmpty()) {
      int[] inputSize = trainingData.getInputSize();
      layer.setInputShape(inputSize[0], inputSize[1], inputSize[2]);

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
    tail = Optional.of(layer);
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
    int[] inputSize = trainingData.getInputSize();
    double[][][] tensor = convertTo3D(input, inputSize[2], inputSize[1], inputSize[0]);
    while (current != null) {
      tensor = current.forward(tensor);
      current = current.next.orElse(null);
    }
    return flatten(tensor);
  }

  @Override
  public void train(double[][] x, double[][] y, double learningRate, int epochs) {
    CNNTrainingSession session = new CNNTrainingSession(null, null, null, null);
    train(x, y, learningRate, epochs, 1, session);
  }

  public void train(double[][] x, double[][] y, double learningRate, int epochs, int batchSize) {
    CNNTrainingSession session = new CNNTrainingSession(null, null, null, null);
    train(x, y, learningRate, epochs, batchSize, session);
  }

  public void train(double[][] x, double[][] y, double learningRate, int epochs, int batchSize,
      CNNTrainingSession session) {
    for (int epoch = 0; epoch < epochs; epoch++) {
      session.setCurrentEpoch(epoch + 1);

      int currentBatch = 0;
      for (int batchStart = 0; batchStart < x.length; batchStart += batchSize) {
        currentBatch++;
        session.setCurrentBatch(currentBatch);
        if (currentBatch % 10 == 0) {
          System.out.print("\r Epoch:" + session.getCurrentEpoch() + " batch:" + currentBatch);
        }

        int batchEnd = Math.min(batchStart + batchSize, x.length);

        DoubleAdder batchLoss = new DoubleAdder();

        // Foward pass
        double[][][] accumulatedGradient = IntStream.range(batchStart, batchEnd)
            .parallel()
            .mapToObj(i -> {
              double[] output = forward(x[i]);

              batchLoss.add(
                  lossFunction.calculateLoss(output, y[i]));

              return convertTo3D(
                  lossFunction.calculateGradient(output, y[i]),
                  output.length, 1, 1);
            })
            .reduce(this::addGradients)
            .orElseThrow();

        session.setLoss(batchLoss.sum() / (batchEnd - batchStart));

        // Apply backward propagation
        for (LayerBase layer = tail.get(); layer != null; layer = layer.prev.orElse(null)) {
          accumulatedGradient = (layer.backward(accumulatedGradient, learningRate));
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

  private double[][][] addGradients(double[][][] a, double[][][] b) {
    if (a == null)
      return b;
    if (b == null)
      return a;

    double[][][] result = new double[a.length][a[0].length][a[0][0].length];

    for (int d = 0; d < a.length; d++) {
      for (int h = 0; h < a[0].length; h++) {
        for (int w = 0; w < a[0][0].length; w++) {
          result[d][h][w] = a[d][h][w] + b[d][h][w];
        }
      }
    }
    return result;
  }
}
