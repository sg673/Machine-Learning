import { useState } from 'react';
import type { CNNModel, Layer } from './types';
import { cnnModelApi } from '../services/api';

interface ModelExportProps {
  model: CNNModel;
  isEditing?: boolean;
  onExport: (model: CNNModel) => void;
}

export function ModelExport({ model, isEditing = false, onExport }: ModelExportProps) {
  const [isExporting, setIsExporting] = useState(false);
  const [exportStatus, setExportStatus] = useState<string>('');

  const handleExport = async () => {
    if (model.layers.length === 0) {
      setExportStatus('Add layers to export model');
      return;
    }

    setIsExporting(true);
    setExportStatus(isEditing ? "Updating model..." : "Saving model...");

    try {
      if (isEditing && model.modelId) {
        await cnnModelApi.update(model.modelId, model);
        setExportStatus('Model updated successfully!');
      }
      else {
        await cnnModelApi.create(model);
        setExportStatus('Model exported successfully!');
      }
      onExport(model);
    } catch (error) {
      setExportStatus('Export failed: ' + (error as Error).message);
    } finally {
      setIsExporting(false);
    }
  };

  const downloadJSON = () => {
    const data = {
      name: model.name,
      architecture: model.layers.map(layer => ({
        type: layer.type,
        config: layer.config,
        connections: layer.connections
      })),
      inputShape: model.inputShape,
      outputSize: model.outputSize
    };

    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${model.name || 'cnn_model'}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="p-4">
      <h3 className="text-lg font-semibold text-text-col mb-4">Model Export</h3>

      <div className="space-y-4">
        <div className="bg-bg border border-border rounded p-3">
          <h4 className="font-medium text-text-col mb-2">Model Summary</h4>
          <div className="text-sm text-text-col-alt space-y-1">
            <div>Name: {model.name || 'Untitled'}</div>
            <div>Layers: {model.layers.length}</div>
            <div>Input: {model.inputShape.join('×')}</div>
            <div>Output: {model.outputSize}</div>
          </div>
        </div>

        <div className="bg-bg border border-border rounded p-3">
          <h4 className="font-medium text-text-col mb-2">Architecture</h4>
          <div className="text-xs text-text-col-alt space-y-1 max-h-32 overflow-y-auto">
            {model.layers.map((layer, idx) => (
              <div key={layer.id} className="flex justify-between">
                <span>{idx + 1}. {layer.type.toUpperCase()}</span>
                <span>{getLayerParams(layer)}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-2">
          <button
            onClick={handleExport}
            disabled={isExporting || model.layers.length === 0}
            className="w-full btn disabled:opacity-50"
          >
            {isExporting ? (isEditing ? 'Updating...' : 'Saving...') : (isEditing ? 'Update' : 'Save')}
          </button>

          <button
            onClick={downloadJSON}
            disabled={model.layers.length === 0}
            className="w-full px-4 py-2 border border-border rounded text-text-col-alt hover:bg-bg-alt disabled:opacity-50"
          >
            Download JSON
          </button>
        </div>

        {exportStatus && (
          <div className={`text-sm p-2 rounded ${exportStatus.includes('success')
            ? 'bg-green-100 text-green-800'
            : exportStatus.includes('failed')
              ? 'bg-red-100 text-red-800'
              : 'bg-blue-100 text-blue-800'
            }`}>
            {exportStatus}
          </div>
        )}

        <div className="text-xs text-text-col-alt">
          <h5 className="font-medium mb-1">Tips:</h5>
          <ul className="space-y-1">
            <li>• Connect layers in sequence</li>
            <li>• Add Flatten before Dense layers</li>
            <li>• Use Dropout for regularization</li>
            <li>• End with appropriate output size</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

function getLayerParams(layer: Layer): string {
  switch (layer.type) {
    case 'conv2d':
      return `${layer.config.filters}@${layer.config.kernelSize}×${layer.config.kernelSize}`;
    case 'maxpool':
    case 'avgpool':
      return `${layer.config.poolSize}×${layer.config.poolSize}`;
    case 'dense':
      return `${layer.config.units}`;
    case 'dropout':
      return `${(layer.config.rate * 100).toFixed(0)}%`;
    default:
      return '';
  }
}
