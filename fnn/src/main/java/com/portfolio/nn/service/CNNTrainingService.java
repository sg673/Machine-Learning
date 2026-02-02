package com.portfolio.nn.service;

import java.io.IOException;
import java.util.LinkedHashMap;
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

import jakarta.annotation.PreDestroy;

@Service
public class CNNTrainingService {

  @Autowired
  private ResultRepo resultRepo;

  private final Map<String, CNNTrainingSession> sessions = new ConcurrentHashMap<>();

  /**
   * LRU cache for recently finished training sessions with capacity limit of 16.
   * Automatically evicts oldest entries when capacity is exceeded.
   */
  private final Map<String, CNNTrainingSession> sessionCache = new LinkedHashMap<String, CNNTrainingSession>(16, 0.75f,
      true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, CNNTrainingSession> eldest) {
      return this.size() > 16;
    }
  };

  @PreDestroy
  public void cleanup(){
    sessions.values().forEach(session -> {
      if(session.isRunning()){
        session.setRunning(false);
        session.setStatus(SessionStatus.STOPPED);
      }
    });
  }

  /**
   * Initiates asynchronous training of a Convolutional Neural Network model.
   * 
   * <p>
   * This method creates a new training session, configures the CNN with the
   * specified
   * layers, loads the training dataset, and starts the training process in a
   * separate thread.
   * The training progress can be monitored using the returned session ID.
   * </p>
   * 
   * <p>
   * The training process includes:
   * </p>
   * <ul>
   * <li>Validation and parsing of the dataset specification</li>
   * <li>Creation of a ConvolutionalNetwork instance</li>
   * <li>Addition of model layers to the network</li>
   * <li>Loading and preprocessing of training data (including one-hot encoding of
   * labels)</li>
   * <li>Execution of the training algorithm with backpropagation</li>
   * <li>Automatic session cleanup and result persistence upon completion</li>
   * </ul>
   * 
   * @param model  the CNN model configuration containing layer definitions, model
   *               ID,
   *               and training dataset specification
   * @param params the training parameters including learning rate, number of
   *               epochs,
   *               and other hyperparameters
   * @return a unique session ID string that can be used to monitor training
   *         progress
   *         and retrieve results
   * @throws RuntimeException if the specified dataset is invalid or not supported
   * 
   * @see CNNModel
   * @see CNNTrainingParameters
   * @see CNNTrainingSession
   */
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
      System.out.println(sessionId + "started");
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
        // TODO implement batches
        network.train(images, labels, params.learningRate, params.epochs);
        trainingEnd(session, SessionStatus.COMPLETED);
      } catch (IOException err) {
        trainingEnd(session, SessionStatus.FAILED, "Dataset Not Recognised");
      }
    }).start();

    return sessionId;
  }

  /**
   * Finalizes a training session with successful completion status.
   * 
   * @param session the training session to terminate
   * @param status  the final status of the training session
   */
  private void trainingEnd(CNNTrainingSession session, SessionStatus status) {
    trainingEnd(session, status, null);
  }

  /**
   * Finalizes a training session and performs cleanup operations.
   * 
   * <p>
   * This method stops the training session, updates its status, persists
   * the results to the repository, and removes the session from active sessions.
   * </p>
   * 
   * @param session the training session to terminate
   * @param status  the final status of the training session
   * @param error   optional error message if the session failed, null for
   *                successful completion
   */
  private void trainingEnd(CNNTrainingSession session, SessionStatus status, String error) {
    session.setRunning(false);
    session.setStatus(status);
    session.Save(resultRepo, error);
    sessions.remove(session.getSessionId());
    sessionCache.put(session.getSessionId(), session);
  }

  /**
   * Retrieves an active or recently finished training session by its ID.
   * 
   * 
   * @param sessionId the unique identifier of the training session
   * @return the CNNTrainingSession if found, null otherwise
   */
  public CNNTrainingSession getSession(String sessionId) {
    if (sessions.containsKey(sessionId)) {
      return sessions.get(sessionId);
    } else {
      return sessionCache.get(sessionId);
    }
  }

  /**
   * Stops an active training session.
   * 
   * <p>
   * Terminates the training process if the session exists and is currently
   * running.
   * The session will be marked as stopped and cleaned up automatically.
   * </p>
   * 
   * @param sessionId the unique identifier of the training session to stop
   * @return true if the session was successfully stopped, false if session
   *         doesn't exist or isn't running
   */
  public boolean stopSession(String sessionId) {
    CNNTrainingSession session = getSession(sessionId);
    if (session != null && session.isRunning()) {
      trainingEnd(session, SessionStatus.STOPPED);
      return true;
    }
    return false;
  }

}
