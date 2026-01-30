import { useState, useEffect } from "react";
import { cnnModelApi, cnnTrainingApi } from "../services/api";
import type { CNNModel } from "../modelBuilder/types";
import { TrainerHeader } from "./TrainerHeader";
import { TrainingParameters } from "./TrainingParameters";
import { TrainingProgress } from "./trainingProgress";
import { TrainingLogs } from "./TrainingLogs";
import type { cnnTrainingSession } from "../services/constants";

export default function ModelTrainer() {
  const [models, setModels] = useState<CNNModel[]>([]);
  const [selectedModelId, setSelectedModelId] = useState<string>("");
  const [epochs, setEpochs] = useState(20);
  const [lr, setLr] = useState(0.001);
  const [batchSize, setBatchSize] = useState(32);
  const [trainingSessionId, setTrainingSessionId] = useState<string | null>(null);
  const [sessionStatus, setSessionStatus] = useState<cnnTrainingSession | null>(null);
  const [isRunning, setIsRunning] = useState<boolean>(false);

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

  const handleModelStart = async () => {
    const params = {
      epochs: epochs,
      batchSize: batchSize,
      learningRate: lr,
    }

    const sessionId = await cnnTrainingApi.start(selectedModelId, params);
    setTrainingSessionId(sessionId);
    handleStatusUpdate();
  }

  const handleStatusUpdate = async () => {
    console.log("Updating status...")
    if (trainingSessionId) {
      const status = await cnnTrainingApi.status(trainingSessionId);
      console.log(status)
      setSessionStatus(status);
      setIsRunning(status.isRunning);
    }
  }

  return (
    <div className="h-screen flex flex-col bg-bg">
      <TrainerHeader
        models={models}
        selectedModelId={selectedModelId}
        onModelChange={setSelectedModelId}
        onModelStart={handleModelStart}
        isRunning={isRunning}
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
