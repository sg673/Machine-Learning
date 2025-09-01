package com.portfolio.fnn.network;

import java.io.*;

public class MNISTReader {
    public static class Dataset {
        private final double[][] images;
        private final int[] labels;

        public Dataset(double[][] images, int[] labels) {
            this.images = images;
            this.labels = labels;
        }

        public double[][] getImages() {
            return images;
        }

        public int[] getLabels() {
            return labels;
        }
    }

    public static Dataset loadTraining() throws IOException {
        return load("/archive/train-images.idx3-ubyte", "/archive/train-labels.idx1-ubyte");
    }

    public static Dataset loadTest() throws IOException {
        return load("/archive/t10k-images.idx3-ubyte", "/archive/t10k-labels.idx1-ubyte");
    }

    private static Dataset load(String imagesPath, String labelsPath) throws IOException {
        double[][] images = readImages(imagesPath);
        int[] labels = readLabels(labelsPath);
        return new Dataset(images, labels);
    }

    private static double[][] readImages(String path) throws IOException {
        try (DataInputStream dis = new DataInputStream(MNISTReader.class.getResourceAsStream(path))) {
            int magicNumber = dis.readInt();
            int numImages = dis.readInt();
            int rows = dis.readInt();
            int cols = dis.readInt();

            double[][] images = new double[numImages][rows * cols];
            for (int i = 0; i < numImages; i++) {
                for (int j = 0; j < rows * cols; j++) {
                    images[i][j] = dis.readUnsignedByte() / 255.0;
                }
            }
            return images;
        }
    }

    private static int[] readLabels(String path) throws IOException {
        try (DataInputStream dis = new DataInputStream(MNISTReader.class.getResourceAsStream(path))) {
            int magicNumber = dis.readInt();
            int numLabels = dis.readInt();

            int[] labels = new int[numLabels];
            for (int i = 0; i < numLabels; i++) {
                labels[i] = dis.readUnsignedByte();
            }
            return labels;
        }
    }

}
