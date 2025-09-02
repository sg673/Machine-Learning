package com.portfolio.fnn.network;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.portfolio.fnn.util.parser.JsonParser.*;

public class ModelSerializer {
    public String saveModel(NeuralNetwork network, ModelMetadata metadata, String modelName, Boolean json) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"));
        Path dir = Paths.get("savedModels", modelName);
        Files.createDirectories(dir);
        String filename = dir.resolve(modelName + "-" + timestamp + (json ? ".json" : ".fnn")).toString();

        try {
            if (json) {
                saveToJson(network, metadata, filename);
            } else {
                saveToBin(network, metadata, filename);
            }
            System.out.println("Model saved to " + modelName);
            return filename;
        } catch (IOException e) {
            System.out.println("Error saving model: " + e.getMessage());
            return "";
        }
    }

    private void saveToJson(NeuralNetwork network, ModelMetadata metadata, String filename) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"metadata\": {\n");
        json.append("    \"epochsTrained\": ").append(metadata.getEpochsTrained()).append(",\n");
        json.append("    \"learningRate\": ").append(metadata.getLearningRate()).append(",\n");
        json.append("    \"finalLoss\": ").append(metadata.getFinalLoss()).append(",\n");
        json.append("    \"testAccuracy\": ").append(metadata.getTestAccuracy()).append(",\n");
        json.append("    \"trainingTimeMs\": ").append(metadata.getTrainingTimeMs()).append(",\n");
        json.append("    \"trainingDate\": \"").append(metadata.getTrainingDate()).append("\",\n");
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int[] layers = network.getLayers();
        for (int i = 0; i < layers.length; i++) {
            sb.append(layers[i]);
            if (i < layers.length - 1) sb.append(", ");
        }
        sb.append("]");
        json.append("    \"layerSizes\": ").append(sb).append("\n");
        json.append("  },\n");
        json.append(" \"weights\": [");

        double[][][] weights = network.getWeights();
        for (int l = 0; l < weights.length; l++) {
            json.append("[");
            for (int i = 0; i < weights[l].length; i++) {
                json.append("[");
                for (int j = 0; j < weights[l][i].length; j++) {
                    json.append(weights[l][i][j]);
                    if (j < weights[l][i].length - 1) json.append(", ");
                }
                json.append("]");
                if (i < weights[l].length - 1) json.append(", \n");
            }
            json.append("]");
            if (l < weights.length - 1) json.append(", \n");
        }
        json.append("], \n \"biases\": [");
        
        double[][] biases = network.getBiases();
        for (int l = 0; l < biases.length; l++) {
            json.append("[");
            for (int j = 0; j < biases[l].length; j++) {
                json.append(biases[l][j]);
                if (j < biases[l].length - 1) json.append(", ");
            }
            json.append("]");
            if (l < biases.length - 1) json.append(",\n");
        }
        json.append("]\n}");
        Files.write(Paths.get(filename), json.toString().getBytes());
    }

    private void saveToBin(NeuralNetwork network, ModelMetadata metadata, String filename) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename))) {
            dos.writeInt(metadata.getEpochsTrained());
            dos.writeDouble(metadata.getLearningRate());
            dos.writeDouble(metadata.getFinalLoss());
            dos.writeDouble(metadata.getTestAccuracy());
            dos.writeLong(metadata.getTrainingTimeMs());
            dos.writeUTF(metadata.getTrainingDate());

            int[] layers = network.getLayers();
            dos.writeInt(layers.length);
            for (int layer : layers) {
                dos.writeInt(layer);
            }
            
            double[][][] weights = network.getWeights();
            for (double[][] layer : weights) {
                for (double[] neuron : layer) {
                    for (double weight : neuron) {
                        dos.writeDouble(weight);
                    }
                }
            }

            double[][] biases = network.getBiases();
            for (double[] layer : biases) {
                for (double bias : layer) {
                    dos.writeDouble(bias);
                }
            }
        }
    }

    public static FFN loadFromJson(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));

        int epochsTrained = parseIntValue(content, "epochsTrained");
        double learningRate = parseDoubleValue(content, "learningRate");
        double finalLoss = parseDoubleValue(content, "finalLoss");
        double testAccuracy = parseDoubleValue(content, "testAccuracy");
        long trainingTimeMs = parseLongValue(content, "trainingTimeMs");
        String trainingDate = parseStringValue(content, "trainingDate");

        String layerSizesStr = parseArrayValue(content, "layerSizes");
        String[] layerSizesParts = layerSizesStr.split(",");
        int[] layers = new int[layerSizesParts.length];
        for (int i = 0; i < layerSizesParts.length; i++) {
            layers[i] = Integer.parseInt(layerSizesParts[i].trim());
        }
        
        FFN ffn = new FFN(layers);
        ffn.getMetadata().setEpochsTrained(epochsTrained);
        ffn.getMetadata().setLearningRate(learningRate);
        ffn.getMetadata().setFinalLoss(finalLoss);
        ffn.getMetadata().setTestAccuracy(testAccuracy);
        ffn.getMetadata().setTrainingTimeMs(trainingTimeMs);
        ffn.getMetadata().setTrainingDate(trainingDate);

        int weightsStart = content.indexOf("\"weights\": [") + 12;
        int weightsEnd = content.indexOf("], \n \"biases\"");
        String weightsStr = content.substring(weightsStart, weightsEnd);
        parseWeights(weightsStr, ffn.getNetwork().getWeights());

        int biasesStart = content.indexOf("\"biases\": [") + 11;
        int biasesEnd = content.lastIndexOf("]");
        String biasesStr = content.substring(biasesStart, biasesEnd);
        parseBiases(biasesStr, ffn.getNetwork().getBiases());

        return ffn;
    }

    public static FFN loadFromBin(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int epochsTrained = dis.readInt();
            double learningRate = dis.readDouble();
            double finalLoss = dis.readDouble();
            double testAccuracy = dis.readDouble();
            long trainingTimeMs = dis.readLong();
            String trainingDate = dis.readUTF();

            int numLayers = dis.readInt();
            int[] layers = new int[numLayers];
            for (int i = 0; i < numLayers; i++) {
                layers[i] = dis.readInt();
            }
            
            FFN ffn = new FFN(layers);
            ffn.getMetadata().setEpochsTrained(epochsTrained);
            ffn.getMetadata().setLearningRate(learningRate);
            ffn.getMetadata().setFinalLoss(finalLoss);
            ffn.getMetadata().setTestAccuracy(testAccuracy);
            ffn.getMetadata().setTrainingTimeMs(trainingTimeMs);
            ffn.getMetadata().setTrainingDate(trainingDate);
            
            double[][][] weights = ffn.getNetwork().getWeights();
            for (int l = 0; l < weights.length; l++) {
                for (int i = 0; i < weights[l].length; i++) {
                    for (int j = 0; j < weights[l][i].length; j++) {
                        weights[l][i][j] = dis.readDouble();
                    }
                }
            }
            
            double[][] biases = ffn.getNetwork().getBiases();
            for (int i = 0; i < biases.length; i++) {
                for (int j = 0; j < biases[i].length; j++) {
                    biases[i][j] = dis.readDouble();
                }
            }
            return ffn;
        }
    }
}