package com.portfolio.nn.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.data.DataLoader;
import com.portfolio.nn.data.MNISTLoader;
import com.portfolio.nn.model.Model;
import com.portfolio.nn.model.TrainingSession;
import com.portfolio.nn.model.modelModel;
import com.portfolio.nn.network.FeedForwardNetwork;
import com.portfolio.nn.repo.ModelRepo;
import com.portfolio.nn.util.DataUtils;

@Service
public class TrainingService {

  @Autowired
  private ModelRepo modelRepo;
  private final Map<String, TrainingSession> sessions = new ConcurrentHashMap<>();

  private final Gson gson = new Gson();

  public String startTraining(modelModel model) {
    String sessionId = UUID.randomUUID().toString();
    String[] layerStrings = model.getLayers().split(",");
    int[] layers = new int[layerStrings.length];
    for (int i = 0; i < layerStrings.length; i++) {
      layers[i] = Integer.parseInt(layerStrings[i].trim());
    }

    FeedForwardNetwork network = new FeedForwardNetwork(
        model.getActivationFunction(),
        layers);

    DataLoader loader;
    if ("MNIST".equals(model.getTrainingData())) {
      loader = new MNISTLoader();
    } else {
      throw new RuntimeException("Dataset not recognised" + model.getTrainingData());
    }

    TrainingSession session = new TrainingSession(sessionId, network, model.getEpochs(), 0, model.getModelName(),
        model.getTrainingData());
    sessions.put(sessionId, session);

    Model saveable = new Model();
    saveable.setId(sessionId);
    saveable.setName(session.getModelName());
    saveable.setType(session.getNetwork().getClass().getName());
    saveable.setLayers(model.getLayers());
    saveable.setActivationFunction(model.getActivationFunction().name());
    final ModelRepo repo = this.modelRepo;
    new Thread(() -> {
      try {

        DataLoader.Dataset dataset = loader.loadTraining();
        double[][] images = dataset.getImages();
        double[][] labels = DataUtils.oneHotEncode(dataset.getLabels());

        int totalBatches = images.length / model.getBatchSize();
        session.setTotalBatches(totalBatches);
        session.setStatus(SessionStatus.TRAINING);
        session.setRunning(true);

        network.train(images, labels, model.getLearningRate(), model.getEpochs(), totalBatches, session);
        session.setStatus(SessionStatus.COMPLETED);
        session.setRunning(false);
        // session.Save(session.getModelName());

        saveable.setBiases(gson.toJson(network.getBiases()));
        saveable.setWeights(gson.toJson(network.getWeights()));
        repo.save(saveable);

      } catch (IOException e) {
        session.setStatus(SessionStatus.FAILED);
        session.setRunning(false);
        // session.Save(session.getModelName());
        repo.save(saveable);

      }
    }).start();

    return sessionId;
  }

  public TrainingSession getSession(String sessionId) {
    return sessions.get(sessionId);
  }

  public boolean stopSession(String sessionId) {
    TrainingSession session = getSession(sessionId);
    if (session != null && session.isRunning()) {
      session.setRunning(false);
      session.setStatus(SessionStatus.STOPPED);
      try {
        session.Save(session.getModelName());
      } catch (IOException ex) {
        System.out.println("Could not save model:" + session.getModelName() + "\n" + ex);
      }
      return true;
    }
    return false;
  }
}
