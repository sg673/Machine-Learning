export type LayerType = 
  | 'conv2d' 
  | 'maxpool' 
  | 'avgpool' 
  | 'dense' 
  | 'flatten' 
  | 'dropout';

export interface Layer {
  id: string;
  type: LayerType;
  position: { x: number; y: number };
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  config: Record<string, any>;
  connections: string[];
}

export interface CNNModel {
  modelId?: string;
  name: string;
  layers: Layer[];
  inputShape: [number, number, number];
  outputSize: number;
  trainingData: string;
  timeCreated?: string;
}

export interface LayerConfig {
  conv2d: {
    filters: number;
    kernelSize: number;
    stride: number;
    padding: number;
    activation: string;
  };
  maxpool: {
    poolSize: number;
    stride: number;
  };
  avgpool: {
    poolSize: number;
    stride: number;
  };
  dense: {
    units: number;
    activation: string;
  };
  //flatten: {};
  dropout: {
    rate: number;
  };
}