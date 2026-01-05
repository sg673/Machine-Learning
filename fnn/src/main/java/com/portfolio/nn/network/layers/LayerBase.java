package com.portfolio.nn.network.layers;

import java.util.Optional;

import com.portfolio.nn.network.activation.ActivationFunction;

/**
 * Abstract base class for all neural network layers in the feed-forward
 * network.
 * Provides common functionality for layer chaining, shape management, and
 * defines
 * the contract for forward and backward propagation.
 */
public abstract class LayerBase {
  /** The activation function used by this layer */
  ActivationFunction activFunc;

  /** Reference to the previous layer in the network */
  public Optional<LayerBase> prev;

  /** Reference to the next layer in the network */
  public Optional<LayerBase> next;

  /** Bias values for this layer's neurons */
  double[] biases;

  /** Input tensor dimensions */
  protected int inputWidth, inputHeight, inputDepth;

  /** Output tensor dimensions */
  protected int outputWidth, outputHeight, outputDepth;

  /** Cached input from the last forward pass, used during backpropagation */
  protected transient double[][][] lastInput;

  /** Cached output from the last forward pass, used during backpropagation */
  protected transient double[][][] lastOutput;

  /**
   * Default constructor initializes the layer with no connections.
   */
  public LayerBase() {
    this.prev = Optional.empty();
    this.next = Optional.empty();
  }

  /**
   * Sets the previous layer in the network chain.
   * 
   * @param layer the layer to set as previous
   */
  public void setPrev(LayerBase layer) {
    this.prev = Optional.of(layer);
  }

  /**
   * Sets the next layer in the network chain.
   * 
   * @param layer the layer to set as next
   */
  public void setNext(LayerBase layer) {
    this.next = Optional.of(layer);
  }

  /**
   * Performs forward propagation through this layer.
   * 
   * @param input the input tensor from the previous layer
   * @return the output tensor after applying this layer's transformation
   */
  public abstract double[][][] forward(double[][][] input);

  /**
   * Performs backward propagation through this layer.
   * 
   * @param gradient     the gradient tensor from the next layer
   * @param learningRate the learning rate for parameter updates
   * @return the gradient tensor to pass to the previous layer
   */
  public abstract double[][][] backward(double[][][] gradient, double learningRate);

  /**
   * Sets the input tensor dimensions and computes the output shape.
   * 
   * @param width  the input tensor width
   * @param height the input tensor height
   * @param depth  the input tensor depth (channels)
   */
  public void setInputShape(int width, int height, int depth) {
    this.inputWidth = width;
    this.inputHeight = height;
    this.inputDepth = depth;
    computeOutputShape();
  }

  /**
   * Gets the output tensor dimensions.
   * 
   * @return array containing [width, height, depth] of the output tensor
   */
  public int[] getOutputShape() {
    return new int[] { outputWidth, outputHeight, outputDepth };
  }

  /**
   * Computes the output tensor dimensions based on the input shape.
   * Must be implemented by subclasses to define layer-specific shape
   * transformations.
   */
  protected abstract void computeOutputShape();
}
