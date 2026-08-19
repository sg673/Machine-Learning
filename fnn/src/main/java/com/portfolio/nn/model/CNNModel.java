package com.portfolio.nn.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.nn.network.activation.ActivationFunction;
import com.portfolio.nn.network.layers.ConvolutionalLayer;
import com.portfolio.nn.network.layers.FCLayer;
import com.portfolio.nn.network.layers.LayerBase;
import com.portfolio.nn.network.layers.PoolingLayer;
import com.portfolio.nn.network.layers.PoolingLayer.PoolingType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Entity representing a Convolutional Neural Network model configuration.
 * 
 * <p>
 * This class defines the structure and parameters of a CNN model including
 * layer definitions, input/output specifications, and training dataset
 * information.
 * Models are persisted to the database and can be used for training sessions.
 * </p>
 */
@Entity
@Table(name = "cnnmodels")
public class CNNModel {
  /**
   * Enumeration of supported CNN layer types.
   * 
   * <p>
   * Each layer type corresponds to a specific neural network layer implementation
   * and defines the JSON serialization value used in API requests.
   * </p>
   */
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

    /**
     * Creates a LayerType from its string representation.
     * 
     * @param value the string value to convert
     * @return the corresponding LayerType
     * @throws IllegalArgumentException if the value doesn't match any LayerType
     */
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

  /**
   * Represents a 2D position coordinate for layer visualization.
   * This is used client-side
   */
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

  /**
   * Represents a single layer in the CNN architecture.
   * 
   * <p>
   * Contains layer configuration, position information for visualization,
   * and connection details for building the network graph.
   * </p>
   */
  public static class Layer {
    public String id;
    public LayerType type;
    public Position position;
    public Map<String, Object> config;
    public List<String> connections;

    /**
     * Converts this layer configuration to a concrete LayerBase implementation.
     * 
     * <p>
     * Creates the appropriate layer instance based on the layer type and
     * configuration parameters. Used during network construction for training.
     * </p>
     * 
     * @return a LayerBase instance configured according to this layer's
     *         specifications
     * @throws IllegalArgumentException if the layer type is not supported or not
     *                                  implemented
     */
    public LayerBase convertToLayerBase() {
      switch (type) {
        case CONV2D:
          return new ConvolutionalLayer(
              (int) config.get("filters"),
              (int) config.get("kernelSize"),
              (int) config.get("stride"),
              (int) config.get("padding"),
              ActivationFunction.fromString((String) config.get("activation")));
        case MAXPOOL:
          return new PoolingLayer(
              (int) config.get("poolSize"),
              (int) config.get("stride"), PoolingType.MAX);
        case AVGPOOL:
          return new PoolingLayer(
              (int) config.get("poolSize"),
              (int) config.get("stride"), PoolingType.AVERAGE);
        case DENSE:
          return new FCLayer(
              (int) config.get("units"),
              ActivationFunction.fromString((String) config.get("activation")));
        case FLATTEN:
          throw new IllegalArgumentException("Layer not implemented: " + type);
        case DROPOUT:
          throw new IllegalArgumentException("Layer not implemented: " + type);
        default:
          throw new IllegalArgumentException("Unsupported LayerType: " + type);
      }
    }
  }

  @Id
  @Column(name = "model_id")
  public String modelId;
  @Column
  public String name;
  @Lob
  @Convert(converter = LayerListConverter.class)
  public List<Layer> layers;
  @Column(name = "input_shape")
  public int[] inputShape;
  @Column(name = "output_size")
  public int outputSize;
  @Column(name = "training_data")
  public String trainingData;
  @Column(name = "time_created")
  public LocalDateTime timeCreated;

  public CNNModel(){
    this.modelId = UUID.randomUUID().toString();
    this.timeCreated = LocalDateTime.now();
  }

  @Converter
  public static class LayerListConverter implements AttributeConverter<List<Layer>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Layer> layers) {
      try {
        return mapper.writeValueAsString(layers);
      } catch (Exception e) {
        return "[]";
      }
    }

    @Override
    public List<Layer> convertToEntityAttribute(String json) {
      try {
        return mapper.readValue(json, new TypeReference<List<Layer>>() {
        });
      } catch (Exception e) {
        return new ArrayList<>();
      }
    }
  }
}
