package com.portfolio.fnn.util;

public class DataUtils {
    public static double[][] oneHotEncode(int[] labels) {
        int numClasses = 10; // For MNIST, we have 10 classes (0-9)
        double[][] oneHot = new double[labels.length][numClasses];
        for (int i = 0; i < labels.length; i++) {
            oneHot[i][labels[i]] = 1.0;
        }
        return oneHot;
    }

    public static int getMaxIndex(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}