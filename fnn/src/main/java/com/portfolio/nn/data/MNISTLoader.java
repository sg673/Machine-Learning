package com.portfolio.nn.data;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * MNIST dataset loader implementation.
 */
public class MNISTLoader implements DataLoader {
    private static final String TRAIN_IMAGES = "/archive/train-images.idx3-ubyte";
    private static final String TRAIN_LABELS = "/archive/train-labels.idx1-ubyte";
    private static final String TEST_IMAGES = "/archive/t10k-images.idx3-ubyte";
    private static final String TEST_LABELS = "/archive/t10k-labels.idx1-ubyte";

    @Override
    public Dataset loadTraining() throws IOException {
        double[][] images = loadImages(TRAIN_IMAGES);
        int[] labels = loadLabels(TRAIN_LABELS);
        return new Dataset(images, labels);
    }

    @Override
    public Dataset loadTest() throws IOException {
        double[][] images = loadImages(TEST_IMAGES);
        int[] labels = loadLabels(TEST_LABELS);
        return new Dataset(images, labels);
    }

    private double[][] loadImages(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(MNISTLoader.class.getResourceAsStream(filename))) {
            @SuppressWarnings("unused")
            int magic = dis.readInt();
            int numImages = dis.readInt();
            int rows = dis.readInt();
            int cols = dis.readInt();

            double[][] images = new double[numImages][rows * cols];
            for (int i = 0; i < numImages; i++) {
                for (int j = 0; j < rows * cols; j++) {
                    images[i][j] = (dis.readUnsignedByte() / 255.0);
                }
            }
            return images;
        }
    }

    private int[] loadLabels(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(MNISTLoader.class.getResourceAsStream(filename))) {
            @SuppressWarnings("unused")
            int magic = dis.readInt();
            int numLabels = dis.readInt();

            int[] labels = new int[numLabels];
            for (int i = 0; i < numLabels; i++) {
                labels[i] = dis.readUnsignedByte();
            }
            return labels;
        }
    }
}