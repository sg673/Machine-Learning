package com.portfolio.nn.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.nn.constants.DataSet;
import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.data.DataLoader;
import com.portfolio.nn.model.CNNModel;
import com.portfolio.nn.model.CNNTrainingParameters;
import com.portfolio.nn.model.CNNTrainingSession;
import com.portfolio.nn.model.CNNModel.Layer;
import com.portfolio.nn.network.ConvolutionalNetwork;
import com.portfolio.nn.repo.ResultRepo;
import com.portfolio.nn.util.DataUtils;

@Service
public class CNNTrainingService {

  @Autowired
  private ResultRepo resultRepo;

  private final Map<String, CNNTrainingSession> sessions = new ConcurrentHashMap<>();

  public String startTraining(CNNModel model, CNNTrainingParameters params) {
    String sessionId = UUID.randomUUID().toString();
    DataSet dataSet;
    try {
      dataSet = DataSet.fromString(model.trainingData);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid dataset specified: " + model.trainingData);
    }
    ConvolutionalNetwork network = new ConvolutionalNetwork(dataSet);
    CNNTrainingSession session = new CNNTrainingSession(network, params, model.modelId, sessionId);
    sessions.put(sessionId, session);

    new Thread(() -> {
      try {
        for (Layer layer : model.layers) {
          network.addLayer(
              layer.convertToLayerBase());
        }
      } catch (IllegalArgumentException err) {
        trainingEnd(session, SessionStatus.FAILED, "LayerType not Recognised");
      }

      DataLoader loader = dataSet.getDataLoader();
      try {
        DataLoader.Dataset data = loader.loadTraining();
        double[][] images = data.getImages();
        double[][] labels = DataUtils.oneHotEncode(data.getLabels());

        session.setStatus(SessionStatus.TRAINING);
        session.setRunning(true);

        network.train(images, labels, params.learningRate, params.epochs);
        trainingEnd(session, SessionStatus.COMPLETED);
      } catch (IOException err) {
        trainingEnd(session, SessionStatus.FAILED, "Dataset Not Recognised");
      }

      /**
       * Init Layers in model done
       * load dataset done
       * start training done
       * save on completion
       */
    });

    return sessionId;
  }

  private void trainingEnd(CNNTrainingSession session, SessionStatus status) {
    session.setRunning(false);
    session.setStatus(status);
    session.Save(resultRepo);
    sessions.remove(session.getSessionId());
  }
  private void trainingEnd(CNNTrainingSession session, SessionStatus status, String error) {
    session.setRunning(false);
    session.setStatus(status);
    session.Save(resultRepo, error);
    sessions.remove(session.getSessionId());
  }

}
