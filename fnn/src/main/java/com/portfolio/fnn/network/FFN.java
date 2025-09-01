package com.portfolio.fnn.network;

import java.util.Random;
import java.io.IOException;

public class FFN {
    private final int[] layers;
    private double[][][] weights;
    private double[][] biases;
    private final Random rand = new Random();

    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private static double dSigmoid(double x) {
        return x * (1.0 - x);
    }

    public FFN(int... layers) {
        this.layers = layers;
        initWeights();
    }

    private void initWeights() {
        weights = new double[layers.length - 1][][];
        biases = new double[layers.length - 1][];

        for (int i = 0; i < layers.length - 1; i++) {
            int in = layers[i];
            int out = layers[i + 1];
            weights[i] = new double[in][out];
            biases[i] = new double[out];

            for (int j = 0; j < in; j++) {
                for (int k = 0; k < out; k++) {
                    weights[i][j][k] = rand.nextGaussian() * 0.01;
                }
            }
            for (int j = 0; j < out; j++) {
                biases[i][j] = 0.0;
            }
        }
    }

    private double[][] forward(double[] input) {
        double[][] activations = new double[layers.length][];
        activations[0] = input.clone();

        for (int i = 1; i < layers.length; i++) {
            int n = layers[i];
            activations[i] = new double[n];

            for (int j = 0; j < n; j++) {
                double sum = biases[i - 1][j];

                for (int k = 0; k < layers[i - 1]; k++) {
                    sum += activations[i - 1][k] * weights[i - 1][k][j];
                }
                activations[i][j] = sigmoid(sum);
            }
        }
        return activations;
    }

    private static double[][] oneHotEncode(int[] labels) {
        int numClasses = 10; // For MNIST, we have 10 classes (0-9)
        double[][] oneHot = new double[labels.length][numClasses];
        for (int i = 0; i < labels.length; i++) {
            oneHot[i][labels[i]] = 1.0;
        }
        return oneHot;
    }

    public void train(double[][] x, double[][] y, double lr, int epochs) {
        LossMonitor monitor = new LossMonitor(1e-6, 5);
        long startTime = System.currentTimeMillis();
        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.print("\rEpoch " + epoch + ": " + monitor.getETA(epoch, epochs));
            double loss = 0.0;

            for (int n = 0; n < x.length; n++) {
                double[][] activations = forward(x[n]);

                double[] yPred = activations[layers.length - 1];
                double[] yTrue = y[n];

                for (int k = 0; k < yTrue.length; k++) {
                    loss += Math.pow(yPred[k] - yTrue[k], 2);
                }
                double[][] deltas = new double[layers.length][];
                deltas[layers.length - 1] = new double[layers[layers.length - 1]];
                for (int k = 0; k < layers[layers.length - 1]; k++) {
                    double yP = yPred[k];
                    double yT = yTrue[k];
                    deltas[layers.length - 1][k] = (yP - yT) * dSigmoid(yP);
                }

                for (int l = layers.length - 2; l > 0; l--) {
                    deltas[l] = new double[layers[l]];
                    for (int k = 0; k < layers[l]; k++) {
                        double sum = 0.0;
                        for (int j = 0; j < layers[l + 1]; j++) {
                            sum += deltas[l + 1][j] * weights[l][k][j];
                        }
                        deltas[l][k] = sum * dSigmoid(activations[l][k]);
                    }
                }

                for (int l = 0; l < layers.length - 1; l++) {
                    for (int i = 0; i < layers[l]; i++) {
                        for (int j = 0; j < layers[l + 1]; j++) {
                            weights[l][i][j] -= lr * activations[l][i] * deltas[l + 1][j];
                        }
                    }
                    for (int j = 0; j < layers[l + 1]; j++) {
                        biases[l][j] -= lr * deltas[l + 1][j];
                    }
                }
            }
            if (epoch % 500 == 0 && epoch != 0) {
                monitor.printStats(epoch, loss / x.length);
            }

            if (monitor.shouldStop(loss / x.length, epoch / 5)) {
                System.out.println("Early stopping at epoch " + epoch + ": " + (loss / x.length));
                break;
            }
        }
        System.out.printf("\nTraining complete in %dm %ds%n", (System.currentTimeMillis() - startTime) / 60000,
                (System.currentTimeMillis() - startTime) % 60000 / 1000);
    }

    public int predict(double[] input) {
        return forward(input)[layers.length - 1][0] > 0.5 ? 1 : 0;
    }

    public int predictClass(double[] input) {
        double[] output = forward(input)[layers.length - 1];
        return getMaxIndex(output);
    }

    private int getMaxIndex(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // private static void xor() {
    // System.out.println("Starting FFN...");
    // FFN ffn = new FFN(2, 4, 1);
    // double[][] x = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
    // double[][] y = { { 0 }, { 1 }, { 1 }, { 0 } };
    // ffn.train(x, y, 0.5, 10000);

    // for (double[] input : x) {
    // int output = ffn.predict(input);
    // System.out.printf("\nInput: %.0f %.0f -> %d%n", input[0], input[1], output);
    // }
    // }

    public void evaluate(double[][] testImages, int[] testLabels) {
        int correct = 0;
        for (int i = 0; i < testImages.length; i++) {
            int predicted = predictClass(testImages[i]);
            int actual = testLabels[i];
            if (predicted == actual) {
                correct++;
            }
        }
        double accuracy = (double) correct / testImages.length;
        System.out.printf("Test accuracy: %.2f%%%n", accuracy * 100);
    }

    private static void mnist() {
        System.out.println("Starting FFN for MNIST...");
        FFN ffn = new FFN(784, 128, 10);
        try {
            MNISTReader.Dataset trainingData = MNISTReader.loadTraining();
            MNISTReader.Dataset testData = MNISTReader.loadTest();

            double[][] trainLabels = oneHotEncode(trainingData.getLabels());
            ffn.train(trainingData.getImages(), trainLabels, 0.01, 10);

            ffn.evaluate(testData.getImages(), testData.getLabels());

        } catch (IOException e) {
            System.out.println("Error loading MNIST data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        mnist();
    }

}
