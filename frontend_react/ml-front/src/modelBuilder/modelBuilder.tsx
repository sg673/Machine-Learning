import { useState, useRef, useCallback } from 'react';
import { LayerPalette } from './LayerPalette';
import { CanvasArea } from './CanvasArea';
import { LayerProperties } from './LayerProperties';
import { ModelExport } from './ModelExport';
import type { Layer, LayerType, CNNModel } from './types';
import { DATASET_CONFIG } from '../services/constants';

export function ModelBuilder() {
  const [layers, setLayers] = useState<Layer[]>([]);
  const [selectedLayer, setSelectedLayer] = useState<Layer | null>(null);
  const [draggedLayer, setDraggedLayer] = useState<LayerType | null>(null);
  const [modelName, setModelName] = useState('');
  const [selectedDataset, setSelectedDataset] = useState<keyof typeof DATASET_CONFIG>("MNIST");
  const canvasRef = useRef<HTMLDivElement>(null);

  const addLayer = useCallback((layerType: LayerType, position: { x: number; y: number }) => {
    const newLayer: Layer = {
      id: `${layerType}_${Date.now()}`,
      type: layerType,
      position,
      config: getDefaultConfig(layerType),
      connections: []
    };
    setLayers(prev => [...prev, newLayer]);
  }, []);

  const updateLayer = useCallback((layerId: string, updates: Partial<Layer>) => {
    setLayers(prev => prev.map(layer =>
      layer.id === layerId ? { ...layer, ...updates } : layer
    ));
    // Update selected layer if it's the one being updated
    setSelectedLayer(prev =>
      prev?.id === layerId ? { ...prev, ...updates } : prev
    );
  }, []);

  const deleteLayer = useCallback((layerId: string) => {
    setLayers(prev => prev.filter(layer => layer.id !== layerId));
    setSelectedLayer(null);
  }, []);

  const connectLayers = useCallback((fromId: string, toId: string) => {
    setLayers(prev => prev.map(layer =>
      layer.id === fromId
        ? { ...layer, connections: [...layer.connections, toId] }
        : layer
    ));
  }, []);

  const buildModel = useCallback((): CNNModel => {
    const sortedLayers = topologicalSort(layers);
    return {
      name: modelName || 'Untitled Model',
      layers: sortedLayers,
      inputShape: DATASET_CONFIG[selectedDataset].inputShape,
      outputSize: getOutputSize(sortedLayers[sortedLayers.length - 1]),
      trainingData: selectedDataset // TODO Placeholder, can be dynamic
    };
  }, [layers, modelName, selectedDataset]);

  return (
    <div className="h-screen flex bg-bg">
      <LayerPalette
        onDragStart={setDraggedLayer}
        onDragEnd={() => setDraggedLayer(null)}
      />

      <div className="flex-1 flex flex-col">
        <div className="bg-bg-alt border-b border-border p-4 flex gap-4 items-center">
          <input
            type="text"
            placeholder="Model Name"
            value={modelName}
            onChange={(e) => setModelName(e.target.value)}
            className="px-3 py-2 bg-bg border border-border rounded text-text-col"
          />
          <div className="flex items-center gap-2">
            <label className="text-text-col-alt text-sm font-medium">
              Dataset:
            </label>
            <select
              value={selectedDataset}
              onChange={(e) => setSelectedDataset(e.target.value as keyof typeof DATASET_CONFIG)}
              className="px-3 py-2 bg-bg border border-border rounded text-text-col ml-4"
            >
              {Object.keys(DATASET_CONFIG).map(dataset => (
                <option key={dataset} value={dataset}>{dataset}</option>
              ))}
            </select>
          </div>

        </div>

        <CanvasArea
          ref={canvasRef}
          layers={layers}
          selectedLayer={selectedLayer}
          draggedLayer={draggedLayer}
          onLayerSelect={setSelectedLayer}
          onLayerAdd={addLayer}
          onLayerUpdate={updateLayer}
          onLayerDelete={deleteLayer}
          onLayerConnect={connectLayers}
        />
      </div>

      <div className="w-80 bg-bg-alt border-l border-border">
        {selectedLayer ? (
          <LayerProperties
            key={selectedLayer.id}
            layer={selectedLayer}
            onUpdate={(updates) => updateLayer(selectedLayer.id, updates)}
            onDelete={() => deleteLayer(selectedLayer.id)}
          />
        ) : (
          <ModelExport
            model={buildModel()}
            onExport={(model) => console.log('Export model:', model)}
          />
        )}
      </div>
    </div>
  );
}

function getDefaultConfig(layerType: LayerType): Record<string, string | number> {
  switch (layerType) {
    case 'conv2d':
      return { filters: 32, kernelSize: 3, stride: 1, padding: 1, activation: 'RELU' };
    case 'maxpool':
      return { poolSize: 2, stride: 2 };
    case 'avgpool':
      return { poolSize: 2, stride: 2 };
    case 'dense':
      return { units: 128, activation: 'RELU' };
    case 'flatten':
      return {};
    case 'dropout':
      return { rate: 0.5 };
    default:
      return {};
  }
}

function topologicalSort(layers: Layer[]): Layer[] {
  const visited = new Set<string>();
  const result: Layer[] = [];

  function visit(layer: Layer) {
    if (visited.has(layer.id)) return;
    visited.add(layer.id);

    layer.connections.forEach(connId => {
      const connLayer = layers.find(l => l.id === connId);
      if (connLayer) visit(connLayer);
    });

    result.unshift(layer);
  }

  layers.forEach(layer => visit(layer));
  return result;
}

function getOutputSize(layer?: Layer): number {
  if (layer?.type === 'dense') {
    return layer.config.units || 10;
  }
  return 10; // Default classification
}