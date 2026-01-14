package com.portfolio.nn.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portfolio.nn.data.DataLoader;
import com.portfolio.nn.data.MNISTLoader;

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

    private DataLoader loader = new MNISTLoader();

    @Override
    public DataLoader getDataLoader() {
      return loader;
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

  public abstract DataLoader getDataLoader();

  public abstract int[] getInputSize();

  public abstract int getOutputSize();

  public abstract String getName();

  public abstract int getTrainingSize();

  public abstract int getTestSize();
}
