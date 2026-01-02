package com.portfolio.nn.network.activation;

/**
 * Activation functions for neural networks.
 */
public enum ActivationFunction {
    SIGMOID {
        @Override
        public double activate(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }
        
        @Override
        public double derivative(double x) {
            double s = activate(x);
            return s * (1 - s);
        }
    },
    
    RELU {
        @Override
        public double activate(double x) {
            return Math.max(0, x);
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1 : 0;
        }
    },
    
    TANH {
        @Override
        public double activate(double x) {
            return Math.tanh(x);
        }
        
        @Override
        public double derivative(double x) {
            double t = activate(x);
            return 1 - t * t;
        }
    },

    NONE {
      @Override
      public double activate(double x) {
        return x;
      }
      @Override
      public double derivative(double x) {
        return 1.0;
      }


    };
    
    public abstract double activate(double x);
    public abstract double derivative(double x);
}