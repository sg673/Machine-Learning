import { useState } from "react";
import type { CNNModel } from "../modelBuilder/types";
import { DropDown } from "../assets/components/Dropdown";

interface ModelPropertiesProps {
  model: CNNModel | null;
  onEditModel: () => void;
  onDeleteModel: (modelId: string) => void;
  onTrainModel: (modelId: string) => void;
  showButtons?: boolean;
}

export function ModelProperties({ model, onEditModel, onDeleteModel, onTrainModel, showButtons=true }: ModelPropertiesProps) {
  const [expandedLayers, setExpandedLayers] = useState<Set<string>>(new Set());

  const toggleLayer = (layerId: string) => {
    setExpandedLayers(prev => {
      const newSet = new Set(prev);
      if (newSet.has(layerId)) {
        newSet.delete(layerId);
      } else {
        newSet.add(layerId);
      }
      return newSet;
    });
  };

  if (!model) {
    return (
      <div className="p-6 text-center text-text-col-alt">
        <p>Select a model to view properties</p>
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col bg-bg-alt border-l border-border">
      <div className="flex-1 p-6 space-y-6 overflow-y-auto">
        <div>
          <h3 className="text-lg font-semibold text-text-col mb-3">Model Information</h3>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-text-col-alt">Name:</span>
              <span className="text-text-col">{model.name}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-text-col-alt">Input Shape:</span>
              <span className="text-text-col">{model.inputShape.join('×')}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-text-col-alt">Output Size:</span>
              <span className="text-text-col">{model.outputSize}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-text-col-alt">Total Layers:</span>
              <span className="text-text-col">{model.layers.length}</span>
            </div>
          </div>
        </div>

        {/* Layer Breakdown */}
        <div>
          <h3 className="text-lg font-semibold text-text-col mb-3">Layer Configuration</h3>
          <div className="space-y-2">
            {model.layers.map((layer, index) => {
              const isExpanded = expandedLayers.has(layer.id);
              return (
                <div key={layer.id} className="bg-bg border border-border rounded p-3">
                  <div
                    className="flex justify-between items-center cursor-pointer"
                    onClick={() => toggleLayer(layer.id)}
                  >
                    <span className="font-medium text-text-col">
                      {index + 1}. {getLayerDisplayName(layer.type)}
                    </span>
                    <div className="flex ">
                      <span className="text-text-col-alt">
                        <DropDown expanded={isExpanded} color={"text-text-col"} />
                      </span>
                    </div>
                  </div>
                  {isExpanded && (
                    <div className="text-xs text-text-col-alt space-y-1 mt-2 pt-2 border-t border-border">
                      {Object.entries(layer.config).map(([key, value]) => (
                        <div key={key} className="flex justify-between">
                          <span>{key}:</span>
                          <span>{String(value)}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Action Buttons - Fixed at bottom */}
      {showButtons &&
        <div className="p-6 pt-0 space-y-3">
          <button
            onClick={onEditModel}
            className="w-full btn-sec"
          >
            Edit Model
          </button>
          <button
            onClick={() => onTrainModel(model.name)}
            className="w-full btn-acc"
          >
            Start Training
          </button>
          <button
            onClick={() => onDeleteModel(model.name)}
            className="w-full btn"
          >
            Delete Model
          </button>
        </div>
      
      }
    </div>
  );
}

function getLayerDisplayName(type: string): string {
  switch (type) {
    case 'conv2d': return 'Convolutional 2D';
    case 'maxpool': return 'Max Pooling';
    case 'avgpool': return 'Average Pooling';
    case 'flatten': return 'Flatten';
    case 'dense': return 'Dense (Fully Connected)';
    case 'dropout': return 'Dropout';
    default: return type;
  }
}