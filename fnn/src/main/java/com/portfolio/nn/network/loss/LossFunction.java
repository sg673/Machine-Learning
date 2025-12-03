package com.portfolio.nn.network.loss;

public interface LossFunction {
  double calculateLoss(double[] predicted, double[] actual);
  double[] calculateGradient(double[] predicted, double[] actual);
}
