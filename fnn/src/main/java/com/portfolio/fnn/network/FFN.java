package com.portfolio.fnn.network;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class FFN {
    private final int[] layers;
    private double[][][] weights;
    private double[][] biases;
    private final Random rand = new Random();

    // metadata fields
    private int epochsTrained = 0;
    private double learningRate = 0.0;
    private double finalLoss = 0.0;
    private double testAccuracy = 0.0;
    private long trainingTimeMs = 0;
    private String trainingDate = "";

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
        this.learningRate = lr;
        this.trainingDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        LossMonitor monitor = new LossMonitor(1e-6, 5);
        long startTime = System.currentTimeMillis();
        for (int epoch = 0; epoch < epochs; epoch++) {
            this.epochsTrained++;
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
                this.finalLoss = loss / x.length;
                monitor.printStats(epoch, loss / x.length);
            }

            if (monitor.shouldStop(loss / x.length, epoch / 5)) {
                this.finalLoss = loss / x.length;
                System.out.println("Early stopping at epoch " + epoch + ": " + (loss / x.length));
                break;
            }
        }
        this.trainingTimeMs = System.currentTimeMillis() - startTime;
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
        this.testAccuracy = (double) correct / testImages.length;
        System.out.printf("Test accuracy: %.2f%%%n", testAccuracy * 100);
    }

    private void saveToJson(String filename) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"metadata\": {\n");
        json.append("    \"epochsTrained\": ").append(epochsTrained).append(",\n");
        json.append("    \"learningRate\": ").append(learningRate).append(",\n");
        json.append("    \"finalLoss\": ").append(finalLoss).append(",\n");
        json.append("    \"testAccuracy\": ").append(testAccuracy).append(",\n");
        json.append("    \"trainingTimeMs\": ").append(trainingTimeMs).append(",\n");
        json.append("    \"trainingDate\": \"").append(trainingDate).append("\",\n");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < layers.length; i++) {
            sb.append(layers[i]);
            if (i < layers.length - 1)
                sb.append(", ");
        }
        sb.append("]");
        json.append("    \"layerSizes\": ").append(sb).append("\n");
        json.append("  },\n");
        json.append("  \"layers\": [");
        for (int i = 0; i < layers.length; i++) {
            json.append(layers[i]);
            if (i < layers.length - 1)
                json.append(", ");
        }
        json.append("],\n \"weights\": [");

        for (int l = 0; l < weights.length; l++) {
            json.append("[");
            for (int i = 0; i < weights[l].length; i++) {
                json.append("[");
                for (int j = 0; j < weights[l][i].length; j++) {
                    json.append(weights[l][i][j]);
                    if (j < weights[l][i].length - 1)
                        json.append(", ");
                }
                json.append("]");
                if (i < weights[l].length - 1)
                    json.append(", \n");
            }
            json.append("]");
            if (l < weights.length - 1)
                json.append(", \n");
        }
        json.append("], \n \"biases\": [");
        for (int l = 0; l < biases.length; l++) {
            json.append("[");
            for (int j = 0; j < biases[l].length; j++) {
                json.append(biases[l][j]);
                if (j < biases[l].length - 1)
                    json.append(", ");
            }
            json.append("]");
            if (l < biases.length - 1)
                json.append(",\n");
        }
        json.append("]\n}");
        Files.write(Paths.get(filename), json.toString().getBytes());

    }

    private void saveToBin(String filename) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename))) {
            dos.writeInt(layers.length);
            for (int layer : layers) {
                dos.writeInt(layer);
            }
            for (double[][] layer : weights) {
                for (double[] neuron : layer) {
                    for (double weight : neuron) {
                        dos.writeDouble(weight);
                    }
                }
            }

            for (double[] layer : biases) {
                for (double bias : layer) {
                    dos.writeDouble(bias);
                }
            }
        }
    }

    public void saveModel(String modelName, Boolean json) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"));
        Path dir = Paths.get("savedModels", modelName);
        Files.createDirectories(dir);
        String filename = dir.resolve(modelName + "-" + timestamp + (json ? ".json" : ".fnn")).toString();

        try {
            if (json) {
                saveToJson(filename);
            } else {
                saveToBin(filename);
            }
            System.out.println("Model saved to " + modelName);
        } catch (IOException e) {
            System.out.println("Error saving model: " + e.getMessage());
        }
    }

    private static void mnist() {
        System.out.println("Starting FFN for MNIST...");
        FFN ffn = new FFN(784, 128, 10);
        try {
            MNISTReader.Dataset trainingData = MNISTReader.loadTraining();
            MNISTReader.Dataset testData = MNISTReader.loadTest();

            double[][] trainLabels = oneHotEncode(trainingData.getLabels());
            ffn.train(trainingData.getImages(), trainLabels, 0.01, 1);
            ffn.evaluate(testData.getImages(), testData.getLabels());
            ffn.saveModel("sample", true);

        } catch (IOException e) {
            System.out.println("Error loading MNIST data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        mnist();
    }

}
