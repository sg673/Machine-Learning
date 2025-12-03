package com.portfolio.nn.network.layers;

import java.util.Optional;

import com.portfolio.nn.network.activation.ActivationFunction;

public abstract class LayerBase {
  ActivationFunction activFunc;
  public Optional<LayerBase> prev;
  public Optional<LayerBase> next;
  double[] biases;
  int size;

  public int inputWidth;
  public int inputHeight;
  public int inputDepth;
  int outputWidth;
  int outputHeight;
  
  public LayerBase(){
    this.prev = Optional.empty();
    this.next = Optional.empty();
  }

  public void setPrev(LayerBase layer){
    this.prev = Optional.of(layer);
  }

  public void setNext(LayerBase layer){
    this.next = Optional.of(layer);
  }

  public abstract double[] forward(double[][][] input);
  public abstract double[] backward(double[] gradient, double learningRate);

  protected double[][][] lastInput;
  protected double[] lastOutput;
}
