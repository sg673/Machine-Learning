package com.portfolio.nn.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DataSet {
  MNIST("MNIST") {
    @Override
    public int[] getInputSize() {
      return new int[] { 28, 28, 1 };
    }

    @Override
    public int getOutputSize() {
      return 10;
    }

    @Override
    public String getName() {
      return "MNIST";
    }

    @Override
    public int getTrainingSize() {
      return 60000;
    }

    @Override
    public int getTestSize() {
      return 10000;
    }
  };

  private final String value;

  DataSet(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static DataSet fromString(String value) {
    for (DataSet type : DataSet.values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown Dataset: " + value);
  }

  public abstract int[] getInputSize();

  public abstract int getOutputSize();

  public abstract String getName();

  public abstract int getTrainingSize();

  public abstract int getTestSize();
}
