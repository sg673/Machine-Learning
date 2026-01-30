export enum PAGES {
  DASHBOARD = 'dashboard',
  MODELS = 'models',
  TRAINING = 'training',
  RESULTS = 'results',
  SETTINGS = 'settings',
}


export type Model = {
  modelName: string,
  trainingData: string,
  epochs: number,
  batchSize: number,
  learningRate: number,
  layers: string,
  activationFunction: string

}
export type Training = {
  sessionId: string,
  status: 'INITIALIZED' | 'TRAINING' | 'COMPLETED' | 'FAILED' | 'STOPPED',
  progress: number,
  epoch: number,
  totalEpochs: number,
  batch: number,
  totalBatches: number,
  accuracy: number
}

export type Result = {
  id: string;
  modelId: string;
  sessionId: string;
  finalAccuracy: number;
  finalLoss: number;
  trainingTime: number;
  epochs: number;
  completedAt: string;
};

export const DATASET_CONFIG = {
  "MNIST": { inputSize: 784, outputSize: 10, inputShape: [28, 28, 1] as [number, number, number] }
}

export type cnnTrainingParameters = {
  epochs: number;
  batchSize: number;
  learningRate: number;
}

export type cnnTrainingSession = {
  sessionId: string;
  status: 'INITIALIZED' | 'TRAINING' | 'COMPLETED' | 'FAILED' | 'STOPPED';
  currentEpoch: number;
  totalEpochs: number;
  currentBatch: number;
  totalBatches: number;
  accuracy: number;
  modelId: string;
  trainingData: string;
  startTime: string;
  isRunning: boolean;
}