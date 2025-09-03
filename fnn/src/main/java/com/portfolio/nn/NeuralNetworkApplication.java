package com.portfolio.nn;

import com.portfolio.nn.data.DataLoader;
import com.portfolio.nn.data.MNISTLoader;
import com.portfolio.nn.network.FeedForwardNetwork;
import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.util.DataUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class NeuralNetworkApplication {

    public static void main(String[] args) {
        // SpringApplication.run(NeuralNetworkApplication.class, args);

        runMNISTDemo();
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

            network.train(trainData.getImages(), trainLabels, 0.01, 10);
            double accuracy = network.evaluate(testData.getImages(), testLabels);

            System.out.println("Test Accuracy: " + (accuracy * 100) + "%");

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}