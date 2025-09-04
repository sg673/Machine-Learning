package com.portfolio.nn.data;

import java.io.IOException;

/**
 * Base interface for data loaders.
 * Provides common methods for loading and preprocessing datasets.
 */
public interface DataLoader {
    
    /**
     * Dataset container for training and test data.
     */
    class Dataset {
        private final double[][] images;
        private final int[] labels;
        
        public Dataset(double[][] images, int[] labels) {
            this.images = images;
            this.labels = labels;
        }
        
        public double[][] getImages() { return images; }
        public int[] getLabels() { return labels; }
    }
    
    /**
     * Load training dataset.
     * @return Training dataset
     * @throws IOException If data loading fails
     */
    Dataset loadTraining() throws IOException;
    
    /**
     * Load test dataset.
     * @return Test dataset
     * @throws IOException If data loading fails
     */
    Dataset loadTest() throws IOException;
}