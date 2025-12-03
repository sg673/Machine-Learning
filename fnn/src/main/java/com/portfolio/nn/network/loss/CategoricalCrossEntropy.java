package com.portfolio.nn.network.loss;

public class CategoricalCrossEntropy implements LossFunction{
  private static final double EPSILON = 1e-15;

  @Override
  public double calculateLoss(double[] predicted, double[] actual){
    double loss = 0.0;
    for (int i = 0; i < predicted.length; i++) {
      double p = Math.max(EPSILON, Math.min(1 - EPSILON, predicted[i]));
      loss -= actual[i] * Math.log(p);
    }
    return loss;
  }

  
  @Override
  public double[] calculateGradient(double[] predicted, double[] actual){
    double[] gradient = new double[predicted.length];
    for (int i = 0; i < predicted.length; i++){
      //TODO Change to utilise epsilon
      gradient[i] = predicted[i] - actual[i];
    }
    return gradient;
  }
}
