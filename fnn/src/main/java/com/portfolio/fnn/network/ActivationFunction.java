package com.portfolio.fnn.network;

public interface ActivationFunction {
    double activate(double x);
    double derivative(double x);
    
    ActivationFunction SIGMOID = new ActivationFunction() {
        @Override
        public double activate(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }
        
        @Override
        public double derivative(double x) {
            return x * (1.0 - x);
        }
    };
    
    ActivationFunction RELU = new ActivationFunction() {
        @Override
        public double activate(double x) {
            return Math.max(0, x);
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1.0 : 0.0;
        }
    };
    
    ActivationFunction TANH = new ActivationFunction() {
        @Override
        public double activate(double x) {
            return Math.tanh(x);
        }
        
        @Override
        public double derivative(double x) {
            return 1.0 - x * x;
        }
    };
    
    ActivationFunction LEAKY_RELU = new ActivationFunction() {
        private final double alpha = 0.01;
        
        @Override
        public double activate(double x) {
            return x > 0 ? x : alpha * x;
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1.0 : alpha;
        }
    };
}