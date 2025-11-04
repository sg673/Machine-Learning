package com.portfolio.nn.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "models")
public class Model {
  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String type;

  @Column
  private String layers;

  @Column(name = "activation_function")
  private String activationFunction;

  @Lob
  @Column
  @JsonIgnore
  private String biases;

  @Lob
  @Column
  @JsonIgnore
  private String weights;

  @Column(name = "create_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getLayers() { return layers; }
    public void setLayers(String layers) { this.layers = layers; }
    
    public String getActivationFunction() { return activationFunction; }
    public void setActivationFunction(String activationFunction) { this.activationFunction = activationFunction; }
    
    public String getWeights() { return weights; }
    public void setWeights(String weights) { this.weights = weights; }
    
    public String getBiases() { return biases; }
    public void setBiases(String biases) { this.biases = biases; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
