package com.portfolio.nn.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.portfolio.nn.constants.DataSet;
import com.portfolio.nn.network.ConvolutionalNetwork;
import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.network.layers.ConvolutionalLayer;
import com.portfolio.nn.network.layers.FCLayer;
import com.portfolio.nn.network.layers.PoolingLayer;

public class CNNTests {

  private ConvolutionalNetwork network;

  @BeforeEach
  void setUp(){
    network = new ConvolutionalNetwork(DataSet.MNIST)
    .addLayer(new ConvolutionalLayer(4,3,1,1,ActivationFunction.RELU))
    .addLayer(new PoolingLayer(2,2,PoolingLayer.PoolingType.MAX))
    .addLayer(new FCLayer(10, ActivationFunction.SIGMOID));
  }


  // addGradients

  @Test
  void addGradients_sumTwoTensorsElementWise(){
    double[][][] a = {{{1.0,2.0},{3.0,4.0}}};
    double[][][] b = {{{5.0,6.0},{7.0,8.0}}};
    double[][][] result = network.addGradientsTest(a, b);
    assertArrayEquals(new double[]{6.0, 8.0}, result[0][0], 1e-9);
    assertArrayEquals(new double[]{10.0, 12.0}, result[0][1], 1e-9);
  }


  // ConvertTo3D

  @Test
  void convertTo3D_mapsValuesCorrectly(){
    double[] input = {1.0,2.0,3.0,4.0};
    double[][][] result = network.convertTo3DTest(input, 1, 2, 2);
    assertEquals(1.0, result[0][0][0]);
    assertEquals(2.0, result[0][0][1]);
    assertEquals(3.0, result[0][1][0]);
    assertEquals(4.0, result[0][1][1]);
  }

  @Test
  void convertTo3D_inputShorterThanVolume_remainderIsZero() {
    double[] input = {1.0, 2.0};
    double[][][] result = network.convertTo3DTest(input, 1, 2, 2);
    assertEquals(0.0, result[0][1][0]);
    assertEquals(0.0, result[0][1][1]);
  }

  // flatten 

  @Test
  void flatten_reconstructsFlatArray() {
    double[][][] tensor = {{{1.0, 2.0}, {3.0, 4.0}}};
    double[] result = network.flattenTest(tensor);
    assertArrayEquals(new double[]{1.0, 2.0, 3.0, 4.0}, result, 1e-9);
  }

  @Test
  void flatten_andConvertTo3D_areInverse() {
    double[] original = new double[784];
    for (int i = 0; i < original.length; i++) original[i] = i * 0.001;
    double[][][] tensor = network.convertTo3DTest(original, 1, 28, 28);
    double[] restored = network.flattenTest(tensor);
    assertArrayEquals(original, restored, 1e-9);
  }
}
