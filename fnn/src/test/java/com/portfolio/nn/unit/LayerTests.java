package com.portfolio.nn.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.network.layers.ConvolutionalLayer;
import com.portfolio.nn.network.layers.FCLayer;
import com.portfolio.nn.network.layers.PoolingLayer;
import com.portfolio.nn.network.layers.PoolingLayer.PoolingType;

public class LayerTests {
  private ConvolutionalLayer convLayer;

  private PoolingLayer poolLayer(PoolingType type) {
    PoolingLayer l = new PoolingLayer(2, 2, type);
    l.setInputShape(4, 4, 1);
    return l;
  }

  private FCLayer FCLayer;

  @BeforeEach
  void setUp() {
    // 4x4 single-channel input, 2 filters, 3x3 kernel, stride 1, no padding
    // output shape: (4 - 3 + 0) / 1 + 1 = 2x2
    convLayer = new ConvolutionalLayer(2, 3, 1, 0, ActivationFunction.RELU);
    convLayer.setInputShape(4, 4, 1);

    FCLayer = new FCLayer(3, ActivationFunction.SIGMOID);
    FCLayer.setInputShape(2, 2, 1);
  }

  // Conv Layer

  @Test
  void convLayer_outputShapeIsCorrect() {
    assertArrayEquals(new int[] { 2, 2, 2 }, convLayer.getOutputShape());
  }

  @Test
  void convLayer_outputDimensionsMatchExpected() {
    double[][][] input = new double[1][4][4];
    double[][][] output = convLayer.forward(input);
    assertEquals(2, output.length);
    assertEquals(2, output[0].length);
    assertEquals(2, output[0][0].length);
  }

  @Test
  void convLayer_reluChangesNegativesToZero() {
    double[][][] input = new double[1][4][4]; // all zeros → bias=0 → sum=0 → relu(0)=0
    double[][][] output = convLayer.forward(input);
    for (double[][] channel : output)
      for (double[] row : channel)
        for (double val : row)
          assertTrue(val >= 0.0);
  }

  @Test
  void convLayer_backwardInputGradientMatchesInputShape() {
    double[][][] input = new double[1][4][4];
    convLayer.forward(input);
    double[][][] gradient = new double[2][2][2];
    double[][][] inputGrad = convLayer.backward(gradient, 0.010);
    assertEquals(1, inputGrad.length);
    assertEquals(4, inputGrad[0].length);
    assertEquals(4, inputGrad[0][0].length);
  }

  // Pooling Layer

  @Test
  void poolLayer_outputShapeIsCorrect() {
    assertArrayEquals(new int[] { 2, 2, 1 }, poolLayer(PoolingType.MAX).getOutputShape());
  }

  @Test
  void poolLayer_maxPoolSelectsMaxValue() {
    PoolingLayer l = poolLayer(PoolingType.MAX);
    double[][][] input = { { { 1.0, 3.0, 0.0, 2.0 },
                         { 2.0, 4.0, 1.0, 0.0 },
                         { 0.0, 1.0, 5.0, 3.0 },
                         { 1.0, 0.0, 2.0, 4.0 } } };
    double[][][] output = l.forward(input);
    assertEquals(4.0, output[0][0][0]); // max of top-left 2x2
    assertEquals(5.0, output[0][1][1]); // max of bottom-left 2x2
  }

  @Test
  void poolLayer_averagePooling_computesMean() {
    PoolingLayer l = poolLayer(PoolingType.AVERAGE);
    double[][][] input = { { { 1.0, 3.0, 2.0, 4.0 },
        { 3.0, 1.0, 4.0, 2.0 },
        { 0.0, 0.0, 0.0, 0.0 },
        { 0.0, 0.0, 0.0, 0.0 } } };
    double[][][] output = l.forward(input);
    assertEquals(2.0, output[0][0][0], 1e-9); // (1+3+3+1)/4
    assertEquals(3.0, output[0][0][1], 1e-9); // (2+4+4+2)/4
    assertEquals(0.0, output[0][1][0], 1e-9);
  }

  @Test
  void poolLayer_maxPooling_backward_routesGradientToMaxPosition() {
    PoolingLayer l = poolLayer(PoolingType.MAX);
    double[][][] input = { { { 1.0, 2.0, 0.0, 0.0 },
        { 3.0, 4.0, 0.0, 0.0 }, // max at [1][1] → flattened idx 3
        { 0.0, 0.0, 0.0, 0.0 },
        { 0.0, 0.0, 0.0, 0.0 } } };
    l.forward(input);
    double[][][] gradient = { { { 1.0, 0.0 }, { 0.0, 0.0 } } };
    double[][][] inputGrad = l.backward(gradient, 0.01);
    assertEquals(1.0, inputGrad[0][1][1]); // gradient flows to max position
    assertEquals(0.0, inputGrad[0][0][0]);
  }

  @Test
  void poolLayer_averagePooling_backward_distributesGradientEvenly() {
    PoolingLayer l = poolLayer(PoolingType.AVERAGE);
    double[][][] input = new double[1][4][4];
    l.forward(input);
    double[][][] gradient = { { { 1.0, 0.0 }, { 0.0, 0.0 } } };
    double[][][] inputGrad = l.backward(gradient, 0.01);
    // gradient of 1.0 split across 2x2 window = 0.25 each
    assertEquals(0.25, inputGrad[0][0][0], 1e-9);
    assertEquals(0.25, inputGrad[0][0][1], 1e-9);
    assertEquals(0.25, inputGrad[0][1][0], 1e-9);
    assertEquals(0.25, inputGrad[0][1][1], 1e-9);
  }

  @Test
  void poolLayer_forward_preservesDepth() {
    PoolingLayer l = new PoolingLayer(2, 2, PoolingType.MAX);
    l.setInputShape(4, 4, 3);
    double[][][] input = new double[3][4][4];
    double[][][] output = l.forward(input);
    assertEquals(3, output.length);
  }

  // FC tests

  @Test
  void FC_outputShape_isCorrect() {
    assertArrayEquals(new int[] { 1, 1, 3 }, FCLayer.getOutputShape());
  }

  @Test
  void FC_forward_outputDimensionsAreCorrect() {
    double[][][] input = new double[1][2][2];
    double[][][] output = FCLayer.forward(input);
    assertEquals(3, output.length);
    assertEquals(1, output[0].length);
    assertEquals(1, output[0][0].length);
  }

  @Test
  void FC_forward_sigmoidOutputIsBetweenZeroAndOne() {
    double[][][] input = new double[1][2][2];
    input[0][0][0] = 1.0;
    input[0][0][1] = -1.0;
    double[][][] output = FCLayer.forward(input);
    for (double[][] neuron : output)
      assertTrue(neuron[0][0] > 0.0 && neuron[0][0] < 1.0);
  }

  @Test
  void FC_forward_deterministicForSameInput() {
    double[][][] input = new double[1][2][2];
    input[0][0][0] = 0.5;
    double[][][] out1 = FCLayer.forward(input);
    double[][][] out2 = FCLayer.forward(input);
    for (int i = 0; i < out1.length; i++)
      assertEquals(out1[i][0][0], out2[i][0][0]);
  }

  @Test
  void FC_backward_inputGradientShapeMatchesInput() {
    double[][][] input = new double[1][2][2];
    FCLayer.forward(input);
    double[][][] gradient = new double[3][1][1];
    gradient[0][0][0] = 1.0;
    double[][][] inputGrad = FCLayer.backward(gradient, 0.01);
    assertEquals(1, inputGrad.length);
    assertEquals(2, inputGrad[0].length);
    assertEquals(2, inputGrad[0][0].length);
  }

  @Test
  void FC_backward_updatesWeights_reducesLossOnSimpleCase() {
    FCLayer singleOutput = new FCLayer(1, ActivationFunction.SIGMOID);
    singleOutput.setInputShape(1, 1, 1);

    double[][][] input = { { { 1.0 } } };
    double[][][] out1 = singleOutput.forward(input);

    // gradient pushing output up
    double[][][] gradient = { { { -1.0 } } };
    singleOutput.backward(gradient, 0.1);

    double[][][] out2 = singleOutput.forward(input);
    assertTrue(out2[0][0][0] > out1[0][0][0]);
  }
}
