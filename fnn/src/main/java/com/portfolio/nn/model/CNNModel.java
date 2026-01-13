package com.portfolio.nn.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class CNNModel {
  public enum LayerType {
    CONV2D("conv2d"),
    MAXPOOL("maxpool"),
    AVGPOOL("avgpool"),
    DENSE("dense"),
    FLATTEN("flatten"),
    DROPOUT("dropout");

    private final String value;

    LayerType(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @JsonCreator
    public static LayerType fromString(String value) {
      for (LayerType type : LayerType.values()) {
        if (type.value.equalsIgnoreCase(value)) {
          return type;
        }
      }
      throw new IllegalArgumentException("Unknown LayerType: " + value);
    }
  }

  public static class Position {
    public double x;
    public double y;

    public Position() {
    }

    public Position(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  public static class Layer {
    public String id;
    public LayerType type;
    public Position position;
    public Map<String, Object> config;
    public List<String> connections;
  }

  public String name;
  public List<Layer> layers;
  public int[] inputShape;
  public int outputSize;
  public String trainingData;
}
