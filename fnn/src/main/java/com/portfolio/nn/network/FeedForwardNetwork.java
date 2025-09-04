package com.portfolio.nn.network;

import com.google.gson.Gson;
import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.util.DataUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.google.gson.GsonBuilder;

/**
 * Feed Forward Neural Network implementation.
 */
public class FeedForwardNetwork implements NeuralNetworkBase {
    private int[] layers;
    private double[][][] weights;
    private double[][] biases;
    private transient final Random rand = new Random();
    private ActivationFunction activationFunction;

    public FeedForwardNetwork(int... layers) {
        this.layers = layers;
        this.activationFunction = ActivationFunction.SIGMOID;
        initWeights();
    }

    public FeedForwardNetwork(ActivationFunction activationFunction, int... layers) {
        this.layers = layers;
        this.activationFunction = activationFunction;
        initWeights();
    }

    /**
     * For Use with loading only,
     * Use other initialisers for new networks
     */
    public FeedForwardNetwork() {

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
        }
    }

    @Override
    public double[] forward(double[] input) {
        double[] current = input.clone();
        for (int i = 0; i < layers.length - 1; i++) {
            double[] next = new double[layers[i + 1]];

            for (int j = 0; j < next.length; j++) {
                double sum = biases[i][j];
                for (int k = 0; k < current.length; k++) {
                    sum += current[k] * weights[i][k][j];
                }
                next[j] = activationFunction.activate(sum);
            }
            current = next;
        }
        return current;
    }

    private double[][] forwardWithActivations(double[] input) {
        double[][] activations = new double[layers.length][];
        activations[0] = input.clone();
        for (int i = 1; i < layers.length; i++) {
            activations[i] = new double[layers[i]];
            for (int j = 0; j < layers[i]; j++) {
                double sum = biases[i - 1][j];
                for (int k = 0; k < layers[i - 1]; k++) {
                    sum += activations[i - 1][k] * weights[i - 1][k][j];
                }
                activations[i][j] = activationFunction.activate(sum);
            }
        }
        return activations;
    }

    @Override
    public int predict(double[] input) {
        double[] output = forward(input);
        return DataUtils.getMaxIndex(output);
    }

    public void train(double[][] x, double[][] y, double learningRate, int epochs, int numBatches) {
        long startTime = System.currentTimeMillis();
        int batchSize = x.length / numBatches;
        int printInterval = Math.max(1, numBatches / 100);

        for (int epoch = 0; epoch < epochs; epoch++) {
            int correctPredictions = 0;
            int totalSamples = 0;

            for (int batch = 0; batch < numBatches; batch++) {
                int start = batch * batchSize;
                int end = (batch == numBatches - 1) ? x.length : start + batchSize;

                double[][][] weightGradients = new double[weights.length][][];
                double[][] biasGradients = new double[biases.length][];
                for (int i = 0; i < weights.length; i++) {
                    weightGradients[i] = new double[weights[i].length][weights[i][0].length];
                    biasGradients[i] = new double[biases[i].length];
                }
                for (int i = start; i < end; i++) {
                    double[][] activations = forwardWithActivations(x[i]);
                    double[] output = activations[layers.length - 1];

                    int predicted = DataUtils.getMaxIndex(output);
                    int actual = DataUtils.getMaxIndex(y[i]);
                    if (predicted == actual) {
                        correctPredictions++;
                    }
                    accumulateGradients(activations, y[i], weightGradients, biasGradients);
                    totalSamples++;
                }
                // update weights + biases
                for (int i = 0; i < weights.length; i++) {
                    for (int j = 0; j < weights[i].length; j++) {
                        for (int k = 0; k < weights[i][j].length; k++) {
                            weights[i][j][k] -= learningRate * weightGradients[i][j][k] / batchSize;
                        }
                    }
                    for (int j = 0; j < biases[i].length; j++) {
                        biases[i][j] -= learningRate * biasGradients[i][j] / batchSize;
                    }
                }
                double accuracy = (double) correctPredictions / totalSamples;
                long elapsed = System.currentTimeMillis() - startTime;
                int totalBatches = epochs * numBatches;
                int completedBatches = epoch * numBatches + batch + 1;
                long avgTimePerBatch = elapsed / completedBatches;
                long eta = avgTimePerBatch * (totalBatches - completedBatches);

                if (batch % printInterval == 0 || batch == numBatches - 1) {
                    System.out.printf(
                            "\rEpoch %d/%d | Batch %d/%d | Accuracy: %.2f%% | ETA: %dm %ds | Elapsed: %dm %ds",
                            epoch + 1, epochs, batch + 1, numBatches, accuracy * 100, eta / 60000, (eta % 60000) / 1000,
                            elapsed / 60000, (elapsed % 60000) / 1000);
                }
            }
        }
        System.out.println();
    }

    @Override
    public void train(double[][] x, double[][] y, double learningRate, int epochs) {
        train(x, y, learningRate, epochs, x.length);
    }

    private void accumulateGradients(double[][] activations, double[] target,
            double[][][] weightGradients, double[][] biasGradients) {
        double[][] deltas = new double[layers.length][];

        // Calculate output layer deltas
        deltas[layers.length - 1] = new double[layers[layers.length - 1]];
        for (int neuron = 0; neuron < layers[layers.length - 1]; neuron++) {
            double output = activations[layers.length - 1][neuron];
            deltas[layers.length - 1][neuron] = (output - target[neuron]) *
                    activationFunction.derivative(output);
        }

        // Calculate hidden layer deltas and accumulate gradients
        for (int i = layers.length - 2; i > 0; i--) {
            deltas[i] = new double[layers[i]];
            for (int j = 0; j < layers[i]; j++) {
                double sum = 0.0;
                for (int k = 0; k < layers[i + 1]; k++) {
                    sum += deltas[i + 1][k] * weights[i][j][k];
                }
                deltas[i][j] = sum * activationFunction.derivative(activations[i][j]);
            }
        }

        // Accumulate weight and bias gradients
        for (int i = 0; i < layers.length - 1; i++) {
            for (int j = 0; j < layers[i]; j++) {
                for (int k = 0; k < layers[i + 1]; k++) {
                    weightGradients[i][j][k] += activations[i][j] * deltas[i + 1][k];
                }
            }
            for (int j = 0; j < layers[i + 1]; j++) {
                biasGradients[i][j] += deltas[i + 1][j];
            }
        }
    }

    @Override
    public double evaluate(double[][] testX, double[][] testY) {
        int correct = 0;
        for (int i = 0; i < testX.length; i++) {
            int predicted = predict(testX[i]);
            int actual = DataUtils.getMaxIndex(testY[i]);
            if (predicted == actual)
                correct++;
        }
        return (double) correct / testX.length;
    }

    @Override
    public void save(String modelName) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"));
        Path dir = Paths.get("savedModels", modelName);
        Files.createDirectories(dir);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        String filename = dir.resolve(modelName + "-" + timestamp + ".json").toString();
        Files.write(Paths.get(filename), json.getBytes());
    }

    @Override
    public FeedForwardNetwork load(String modelName) throws IOException {
        Path dir = Paths.get("savedModels", modelName);
        if (!Files.exists(dir)) {
            throw new IOException("Model directory not found: " + dir);
        }

        Path mostRecent = Files.list(dir)
                .filter(path -> path.toString().endsWith(".json"))
                .max((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .orElseThrow(() -> new IOException("No model files found in: " + dir));

        String json = new String(Files.readAllBytes(mostRecent));
        return new Gson().fromJson(json, FeedForwardNetwork.class);
    }

    public FeedForwardNetwork load(String modelName, String filename) throws IOException {
        Path dir = Paths.get("savedModels", modelName);
        if (!Files.exists(dir)) {
            throw new IOException("Model directory not found: " + dir);
        }
        try {
            String json = new String(Files.readAllBytes(Paths.get("savedModels", modelName, filename)));
            return new Gson().fromJson(json, FeedForwardNetwork.class);
        } catch (IOException e) {
            System.out.println(e);
            throw new IOException("Error loading model from file: " + filename, e);
        }
    }

    // Getters
    public int[] getLayers() {
        return layers;
    }

    public double[][][] getWeights() {
        return weights;
    }

    public double[][] getBiases() {
        return biases;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }
}