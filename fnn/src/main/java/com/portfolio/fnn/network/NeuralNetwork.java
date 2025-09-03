package com.portfolio.fnn.network;

import java.util.Arrays;
import java.util.Random;
import static com.portfolio.fnn.util.DataUtils.*;

public class NeuralNetwork {
    private final int[] layers;
    private double[][][] weights;
    private double[][] biases;
    private final Random rand = new Random();
    private ActivationFunction activationFunction;

    public NeuralNetwork(int... layers) {
        this.layers = layers;
        this.activationFunction = ActivationFunction.SIGMOID; // Default
        initWeights();
    }
    
    public NeuralNetwork(ActivationFunction activationFunction, int... layers) {
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
            for (int j = 0; j < out; j++) {
                biases[i][j] = 0.0;
            }
        }
    }

    public double[][] forward(double[] input) {
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
                activations[i][j] = activationFunction.activate(sum);
            }
        }
        return activations;
    }

    public int predict(double[] input) {
        return forward(input)[layers.length - 1][0] > 0.5 ? 1 : 0;
    }

    public int predictClass(double[] input) {
        double[] output = forward(input)[layers.length - 1];
        return getMaxIndex(output);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof NeuralNetwork)) {
            return false;
        }
        NeuralNetwork that = (NeuralNetwork) other;
        return Arrays.equals(this.layers, that.layers) && Arrays.deepEquals(this.biases, that.biases)
                && Arrays.deepEquals(this.weights, that.weights);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(layers);
        result = 31 * result + Arrays.deepHashCode(weights);
        result = 31 * result + Arrays.deepHashCode(biases);
        return result;
    }

    // Getters and Setters
    public int[] getLayers() { return layers; }
    public double[][][] getWeights() { return weights; }
    public double[][] getBiases() { return biases; }
    public ActivationFunction getActivationFunction() { return activationFunction; }
    public void setWeights(double[][][] weights) { this.weights = weights; }
    public void setBiases(double[][] biases) { this.biases = biases; }
    public void setActivationFunction(ActivationFunction activationFunction) { this.activationFunction = activationFunction; }
}