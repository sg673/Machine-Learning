package com.portfolio.nn.network.activation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Activation functions for neural networks.
 */
public enum ActivationFunction {
  SIGMOID("SIGMOID") {
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

  RELU("RELU") {
    @Override
    public double activate(double x) {
      return Math.max(0, x);
    }

    @Override
    public double derivative(double x) {
      return x > 0 ? 1 : 0;
    }
  },

  TANH("TANH") {
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

  NONE("NONE") {
    @Override
    public double activate(double x) {
      return x;
    }

    @Override
    public double derivative(double x) {
      return 1.0;
    }
  };

  private final String value;

  ActivationFunction(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ActivationFunction fromString(String value) {
    for (ActivationFunction type : ActivationFunction.values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown function: " + value);
  }

  public abstract double activate(double x);

  public abstract double derivative(double x);
}