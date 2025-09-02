package com.portfolio.fnn.network;

public class ActivationFunction {
    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double dSigmoid(double x) {
        return x * (1.0 - x);
    }
}