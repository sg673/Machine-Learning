package com.portfolio.fnn.network;

import java.io.IOException;

import static com.portfolio.fnn.util.DataUtils.*;

public class FFN {
    private final NeuralNetwork network;
    private final NetworkTrainer trainer;
    private final ModelSerializer serializer;
    private final NetworkEvaluator evaluator;
    private final ModelMetadata metadata;

    public FFN(int... layers) {
        this.network = new NeuralNetwork(layers);
        this.trainer = new NetworkTrainer();
        this.serializer = new ModelSerializer();
        this.evaluator = new NetworkEvaluator();
        this.metadata = new ModelMetadata();
    }

    public FFN(ActivationFunction activationFunction, int... layers) {
        this.network = new NeuralNetwork(activationFunction, layers);
        this.trainer = new NetworkTrainer();
        this.serializer = new ModelSerializer();
        this.evaluator = new NetworkEvaluator();
        this.metadata = new ModelMetadata();
    }

    public void train(double[][] x, double[][] y, double lr, int epochs) {
        trainer.train(network, metadata, x, y, lr, epochs);
    }

    public int predict(double[] input) {
        return network.predict(input);
    }

    public int predictClass(double[] input) {
        return network.predictClass(input);
    }

    public void evaluate(double[][] testImages, int[] testLabels) {
        double accuracy = evaluator.evaluate(network, testImages, testLabels);
        metadata.setTestAccuracy(accuracy);
    }

    public String saveModel(String modelName, Boolean json) throws IOException {
        return serializer.saveModel(network, metadata, modelName, json);
    }

    public static FFN loadFromJson(String filename) throws IOException {
        return ModelSerializer.loadFromJson(filename);
    }

    public static FFN loadFromBin(String filename) throws IOException {
        return ModelSerializer.loadFromBin(filename);
    }

    private static void mnist() {
        System.out.println("Starting FFN for MNIST...");
        FFN ffn = new FFN(ActivationFunction.RELU, 784, 128, 10);
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

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FFN)) {
            return false;
        }
        FFN that = (FFN) other;
        return this.network.equals(that.network);
    }

    @Override
    public int hashCode() {
        return network.hashCode();
    }

    // Getters for components
    public NeuralNetwork getNetwork() {
        return network;
    }

    public ModelMetadata getMetadata() {
        return metadata;
    }

    public static void main(String[] args) {
        mnist();
    }

}
