package com.portfolio.nn.constants;

public enum DataSet {
  MNIST {
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

  public abstract int[] getInputSize();

  public abstract int getOutputSize();

  public abstract String getName();

  public abstract int getTrainingSize();

  public abstract int getTestSize();
}
