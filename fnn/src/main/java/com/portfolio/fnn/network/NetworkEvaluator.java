package com.portfolio.fnn.network;

public class NetworkEvaluator {
    public double evaluate(NeuralNetwork network, double[][] testImages, int[] testLabels) {
        int correct = 0;
        for (int i = 0; i < testImages.length; i++) {
            int predicted = network.predictClass(testImages[i]);
            int actual = testLabels[i];
            if (predicted == actual) {
                correct++;
            }
        }
        double accuracy = (double) correct / testImages.length;
        System.out.printf("Test accuracy: %.2f%%%n", accuracy * 100);
        return accuracy;
    }
}