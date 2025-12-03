package com.portfolio.nn;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.portfolio.nn.data.DataLoader;
import com.portfolio.nn.data.MNISTLoader;
import com.portfolio.nn.network.ConvolutionalNetwork;
import com.portfolio.nn.network.FeedForwardNetwork;
import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.network.layers.ConvolutionalLayer;
import com.portfolio.nn.network.layers.FCLayer;
import com.portfolio.nn.network.layers.PoolingLayer;
import com.portfolio.nn.network.layers.PoolingLayer.PoolingType;
import com.portfolio.nn.util.DataUtils;

@SuppressWarnings("unused")
@SpringBootApplication
public class NeuralNetworkApplication {
  public static void main(String[] args) {
    // SpringApplication.run(NeuralNetworkApplication.class, args);

    // runMNISTDemo();
    runCNNDemo();
  }

  private static void runMNISTDemo() {
    System.out.println("Starting Neural Network Framework - MNIST Demo");

    try {
      DataLoader loader = new MNISTLoader();
      DataLoader.Dataset trainData = loader.loadTraining();
      DataLoader.Dataset testData = loader.loadTest();

      FeedForwardNetwork network = new FeedForwardNetwork(
          ActivationFunction.RELU, 784, 128, 10);

      double[][] trainLabels = DataUtils.oneHotEncode(trainData.getLabels());
      double[][] testLabels = DataUtils.oneHotEncode(testData.getLabels());

      network.train(trainData.getImages(), trainLabels, 0.02, 1);
      double accuracy = network.evaluate(testData.getImages(), testLabels);
      System.out.println("Test Accuracy: " + (accuracy * 100) + "%");
      // network.save("t1");

    } catch (IOException e) {
      System.err.println("Error loading data: " + e);
    }
  }

  private static void runCNNDemo() {
    System.out.println("Starting Neural Network Framework - CNN Demo");

    try {
      DataLoader loader = new MNISTLoader();
      DataLoader.Dataset trainData = loader.loadTraining();
      DataLoader.Dataset testData = loader.loadTest();

      ConvolutionalNetwork cnn = new ConvolutionalNetwork();

      // 28x28x1 -> 24x24x32 -> 12x12x32 -> 8x8x64 -> 4x4x64 -> 128 -> 10
      // cnn.addLayer(
      // new ConvolutionalLayer(28,28,1,32,5,1,0,ActivationFunction.RELU)
      // ).addLayer(
      // new PoolingLayer(24,24,32,2,2,PoolingLayer.PoolingType.MAX)
      // ).addLayer(
      // new ConvolutionalLayer(12,12,32,64,5,1,0,ActivationFunction.RELU)
      // ).addLayer(
      // new PoolingLayer(8,8,64,2,2,PoolingLayer.PoolingType.MAX)
      // ).addLayer(
      // new FCLayer(4,4,64,128, ActivationFunction.RELU)
      // ).addLayer(
      // new FCLayer(1,1,128,10,ActivationFunction.SIGMOID)
      // );

      cnn.addLayer(
          new ConvolutionalLayer(32, 3, 1, 0, ActivationFunction.RELU))
          .addLayer(
              new ConvolutionalLayer(32, 3, 1, 0, ActivationFunction.RELU))
          .addLayer(
              new PoolingLayer(2, 1, PoolingType.MAX))
          .addLayer(
              new ConvolutionalLayer(64, 3, 1, 0, ActivationFunction.RELU))
          .addLayer(
              new PoolingLayer(2, 1, PoolingType.MAX))
          .addLayer(
              new FCLayer(128, ActivationFunction.RELU))
          .addLayer(
              new FCLayer(10, ActivationFunction.SIGMOID));

      cnn.train(trainData.getImages(), DataUtils.oneHotEncode(trainData.getLabels()), 0.001, 1);
      double accuracy = cnn.evaluate(testData.getImages(), DataUtils.oneHotEncode(testData.getLabels()));
      System.out.println("Accuracy: " + (accuracy * 100) + "%");

    } catch (IOException e) {
      System.err.println("Error loading data: " + e);
    }
  }
}