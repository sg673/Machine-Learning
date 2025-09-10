package com.portfolio.nn.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.portfolio.nn.constants.SessionStatus;
import com.portfolio.nn.data.DataLoader;
import com.portfolio.nn.data.MNISTLoader;
import com.portfolio.nn.model.TrainingSession;
import com.portfolio.nn.model.modelModel;
import com.portfolio.nn.network.FeedForwardNetwork;
import com.portfolio.nn.util.DataUtils;

@Service
public class TrainingService {
    private final Map<String, TrainingSession> sessions = new ConcurrentHashMap<>();

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

        new Thread(() -> {
            try {
                DataLoader.Dataset dataset = loader.loadTraining();
                double[][] images = dataset.getImages();
                double[][] labels = DataUtils.oneHotEncode(dataset.getLabels());

                int totalBatches = images.length / model.getBatchSize();

                TrainingSession session = new TrainingSession(sessionId, network, model.getEpochs(), totalBatches);
                session.setStatus(SessionStatus.TRAINING);
                session.setRunning(true);
                sessions.put(sessionId, session);

                network.train(images, labels, model.getLearningRate(), model.getEpochs(), totalBatches, session);
                session.setStatus(SessionStatus.COMPLETED);
                session.setRunning(false);

            } catch (IOException e) {
                TrainingSession session = getSession(sessionId);
                if (session != null) {
                    session.setStatus(SessionStatus.FAILED);
                    session.setRunning(false);
                }
            }
        }).start();

        return sessionId;
    }

    public TrainingSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
}
