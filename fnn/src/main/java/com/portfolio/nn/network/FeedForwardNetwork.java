package com.portfolio.nn.network;

import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.util.DataUtils;
import java.util.Random;

/**
 * Feed Forward Neural Network implementation.
 */
public class FeedForwardNetwork implements NeuralNetworkBase {
    private final int[] layers;
    private double[][][] weights;
    private double[][] biases;
    private final Random rand = new Random();
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

    private void backpropagate(double[][] activations, double[] target, double learningRate) {
        double[][] deltas = new double[layers.length][];

        // output layer
        deltas[layers.length - 1] = new double[layers[layers.length - 1]];
        for (int neuron = 0; neuron < layers[layers.length - 1]; neuron++) {
            double output = activations[layers.length - 1][neuron];
            deltas[layers.length - 1][neuron] = (output - target[neuron]) *
                    activationFunction.derivative(output);
        }
        // Hidden layers
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
        // update weights and biases
        for (int i = 0; i < layers.length - 1; i++) {
            for (int j = 0; j < layers[i]; j++) {
                for (int k = 0; k < layers[i + 1]; k++) {
                    weights[i][j][k] -= learningRate * activations[i][j] * deltas[i + 1][k];
                }
            }
            for (int j = 0; j < layers[i + 1]; j++) {
                biases[i][j] -= learningRate * deltas[i + 1][j];
            }
        }

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
            double totalLoss = 0.0;

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
                    for (int j = 0; j < y[i].length; j++) {
                        totalLoss += Math.pow(output[j] - y[i][j], 2);
                    }

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