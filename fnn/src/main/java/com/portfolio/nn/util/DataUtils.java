package com.portfolio.nn.util;

/**
 * Utility methods for data processing.
 */
public class DataUtils {
    
    /**
     * Convert labels to one-hot encoded format.
     * @param labels Array of integer labels
     * @return One-hot encoded labels
     */
    public static double[][] oneHotEncode(int[] labels) {
        int maxLabel = 0;
        for (int label : labels) {
            maxLabel = Math.max(maxLabel, label);
        }
        
        double[][] encoded = new double[labels.length][maxLabel + 1];
        for (int i = 0; i < labels.length; i++) {
            encoded[i][labels[i]] = 1.0;
        }
        return encoded;
    }
    
    /**
     * Find index of maximum value in array.
     * @param array Input array
     * @return Index of maximum value
     */
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