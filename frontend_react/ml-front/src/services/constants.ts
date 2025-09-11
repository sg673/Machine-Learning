export enum PAGES {
    DASHBOARD = 'dashboard',
    MODELS = 'models',
    TRAINING = 'training',
    RESULTS = 'results',
    SETTINGS = 'settings',
}


export type model_values = {
    modelName:string,
    trainingData:string,
    epochs:number,
    batchSize:number,
    learningRate:number,
    layers: string,
    activationFunction:string

}

export const DATASET_CONFIG = {
    "MNIST": {inputSize:784, outputSize:10}
}