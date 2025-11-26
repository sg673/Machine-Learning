package com.portfolio.nn.network.layers;

import java.util.Optional;

import com.portfolio.nn.network.activation.ActivationFunction;

public abstract class LayerBase {
  ActivationFunction activFunc;
  Optional<LayerBase> prev;
  Optional<LayerBase> next;
  double[] biases;
  int size;

  int inputWidth;
  int inputHeight;
  int inputDepth;
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
}
