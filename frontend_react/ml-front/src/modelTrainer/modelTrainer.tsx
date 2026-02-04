import { useState, useEffect } from "react";
import { cnnModelApi, cnnTrainingApi } from "../services/api";
import type { CNNModel } from "../modelBuilder/types";
import { TrainerHeader } from "./trainerHeader";
import { TrainingParameters } from "./trainingParameters";
import { TrainingProgress } from "./trainingProgress";
import { TrainingLogs } from "./trainingLogs";
import type { cnnTrainingSession } from "../services/constants";

export default function ModelTrainer() {
  const [models, setModels] = useState<CNNModel[]>([]);
  const [selectedModelId, setSelectedModelId] = useState<string>("");
  const [epochs, setEpochs] = useState(5);
  const [lr, setLr] = useState(0.01);
  const [batchSize, setBatchSize] = useState(32);
  const [trainingSessionId, setTrainingSessionId] = useState<string | null>(null);
  const [sessionStatus, setSessionStatus] = useState<cnnTrainingSession | null>(null);

  useEffect(() => {
    const fetchModels = async () => {
      try {
        const fetchedModels = await cnnModelApi.getAll();
        setModels(fetchedModels);
        if (fetchedModels.length > 0) {
          setSelectedModelId(fetchedModels[0].modelId || "");
        }
      } catch (error) {
        console.error("Failed to fetch models:", error);
      }
    };
    fetchModels();
  }, []);

  useEffect(() => {
  if (!trainingSessionId) return;

  const interval = setInterval(async () => {
    try {
      const status = await cnnTrainingApi.status(trainingSessionId);
      setSessionStatus(status);
      //console.log(status);

      if (
        status.status === "COMPLETED" ||
        status.status === "FAILED"
      ) {
        clearInterval(interval);
      }
    } catch (error) {
      console.error(error);
      clearInterval(interval);
    }
  }, 200);

  return () => clearInterval(interval);
}, [trainingSessionId]);

  const handleModelStart = async () => {
    try {
      const params = { epochs, batchSize, learningRate: lr };
      const response = await cnnTrainingApi.start(selectedModelId, params);
      setTrainingSessionId(response);
    } catch (error) {
      console.error("Failed to start training:", error);
    }
  };

  const handleSessionStop = async () => {
    if (trainingSessionId) {
      try {
        await cnnTrainingApi.stop(trainingSessionId);
      } catch (error) {
        console.error("Failed to stop training:", error);
      }
    }
  };

  return (
    <div className="h-screen flex flex-col bg-bg">
      <TrainerHeader
        models={models}
        selectedModelId={selectedModelId}
        onModelChange={setSelectedModelId}
        onModelStart={handleModelStart}
        onTrainingStop={handleSessionStop}
        isRunning={sessionStatus?.isRunning ? sessionStatus.isRunning : false}
      />

      <main className="flex flex-1 overflow-hidden">
        <TrainingParameters
          epochs={epochs}
          lr={lr}
          batchSize={batchSize}
          onEpochsChange={setEpochs}
          onLrChange={setLr}
          onBatchSizeChange={setBatchSize}
        />
        <TrainingProgress
          sessionStatus={sessionStatus}
        />
      </main>

      <TrainingLogs />
    </div>
  );
}
