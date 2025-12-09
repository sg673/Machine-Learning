package com.portfolio.nn.network.layers;

import java.util.Optional;

import com.portfolio.nn.network.activation.ActivationFunction;

public abstract class LayerBase {
  ActivationFunction activFunc;
  public Optional<LayerBase> prev;
  public Optional<LayerBase> next;
  double[] biases;

  protected int inputWidth, inputHeight, inputDepth;
  protected int outputWidth, outputHeight, outputDepth;

  public LayerBase() {
    this.prev = Optional.empty();
    this.next = Optional.empty();
  }

  public void setPrev(LayerBase layer) {
    this.prev = Optional.of(layer);
  }

  public void setNext(LayerBase layer) {
    this.next = Optional.of(layer);
  }

  public abstract double[][][] forward(double[][][] input);

  public abstract double[][][] backward(double[][][] gradient, double learningRate);

  public void setInputShape(int width, int height, int depth) {
    this.inputWidth = width;
    this.inputHeight = height;
    this.inputDepth = depth;
    computeOutputShape();
  }

  public int[] getOutputShape() {
    return new int[] { outputWidth, outputHeight, outputDepth };
  }

  protected abstract void computeOutputShape();

  protected double[][][] lastInput;
  protected double[][][] lastOutput;
}
