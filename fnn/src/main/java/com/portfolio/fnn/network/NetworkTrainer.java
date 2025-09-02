package com.portfolio.fnn.network;

import static com.portfolio.fnn.network.ActivationFunction.*;

public class NetworkTrainer {
    public void train(NeuralNetwork network, ModelMetadata metadata, double[][] x, double[][] y, double lr, int epochs) {
        metadata.setTrainingStart(lr);
        LossMonitor monitor = new LossMonitor(1e-6, 5);
        long startTime = System.currentTimeMillis();
        
        int[] layers = network.getLayers();
        double[][][] weights = network.getWeights();
        double[][] biases = network.getBiases();

        for (int epoch = 0; epoch < epochs; epoch++) {
            metadata.incrementEpoch();
            System.out.print("\rEpoch " + epoch + ": " + monitor.getETA(epoch, epochs));
            double loss = 0.0;

            for (int n = 0; n < x.length; n++) {
                double[][] activations = network.forward(x[n]);

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
                double avgLoss = loss / x.length;
                metadata.setTrainingComplete(avgLoss, System.currentTimeMillis() - startTime);
                monitor.printStats(epoch, avgLoss);
            }

            if (monitor.shouldStop(loss / x.length, epoch / 5)) {
                metadata.setTrainingComplete(loss / x.length, System.currentTimeMillis() - startTime);
                System.out.println("Early stopping at epoch " + epoch + ": " + (loss / x.length));
                break;
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        metadata.setTrainingComplete(metadata.getFinalLoss(), totalTime);
        System.out.printf("\nTraining complete in %dm %ds%n", totalTime / 60000, totalTime % 60000 / 1000);
    }
}