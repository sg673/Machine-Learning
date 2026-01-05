import type { Layer } from './types';

interface LayerPropertiesProps {
  layer: Layer;
  onUpdate: (updates: Partial<Layer>) => void;
  onDelete: () => void;
}

export function LayerProperties({ layer, onUpdate, onDelete }: LayerPropertiesProps) {
  const updateConfig = (key: string, value: string | number) => {
    onUpdate({
      config: { ...layer.config, [key]: value }
    });
  };

  return (
    <div className="p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-text-col">Layer Properties</h3>
        <button
          onClick={onDelete}
          className="text-red-500 hover:text-red-700 text-sm"
        >
          Delete
        </button>
      </div>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-text-col-alt mb-1">
            Layer Type
          </label>
          <div className="px-3 py-2 bg-bg border border-border rounded text-text-col-alt">
            {layer.type.toUpperCase()}
          </div>
        </div>

        {renderLayerSpecificConfig(layer, updateConfig)}

        <div>
          <label className="block text-sm font-medium text-text-col-alt mb-1">
            Position
          </label>
          <div className="grid grid-cols-2 gap-2">
            <input
              type="number"
              value={Math.round(layer.position.x)}
              onChange={(e) => onUpdate({ 
                position: { ...layer.position, x: parseInt(e.target.value) || 0 }
              })}
              className="px-2 py-1 bg-bg border border-border rounded text-text-col-alt text-sm"
              placeholder="X"
            />
            <input
              type="number"
              value={Math.round(layer.position.y)}
              onChange={(e) => onUpdate({ 
                position: { ...layer.position, y: parseInt(e.target.value) || 0 }
              })}
              className="px-2 py-1 bg-bg border border-border rounded text-text-col-alt text-sm"
              placeholder="Y"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function renderLayerSpecificConfig(layer: Layer, updateConfig: (key: string, value: string | number) => void) {
  switch (layer.type) {
    case 'conv2d':
      return (
        <>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Filters</label>
            <input
              type="number"
              value={layer.config.filters}
              onChange={(e) => updateConfig('filters', parseInt(e.target.value) || 1)}
              min="1"
              max="512"
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Kernel Size</label>
            <select
              value={layer.config.kernelSize}
              onChange={(e) => updateConfig('kernelSize', parseInt(e.target.value))}
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            >
              <option value={1}>1x1</option>
              <option value={3}>3x3</option>
              <option value={5}>5x5</option>
              <option value={7}>7x7</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Stride</label>
            <input
              type="number"
              value={layer.config.stride}
              onChange={(e) => updateConfig('stride', parseInt(e.target.value) || 1)}
              min="1"
              max="4"
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Padding</label>
            <input
              type="number"
              value={layer.config.padding}
              onChange={(e) => updateConfig('padding', parseInt(e.target.value) || 0)}
              min="0"
              max="10"
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Activation</label>
            <select
              value={layer.config.activation}
              onChange={(e) => updateConfig('activation', e.target.value)}
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            >
              <option value="RELU">ReLU</option>
              <option value="SIGMOID">Sigmoid</option>
              <option value="TANH">Tanh</option>
            </select>
          </div>
        </>
      );

    case 'maxpool':
    case 'avgpool':
      return (
        <>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Pool Size</label>
            <select
              value={layer.config.poolSize}
              onChange={(e) => updateConfig('poolSize', parseInt(e.target.value))}
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            >
              <option value={2}>2x2</option>
              <option value={3}>3x3</option>
              <option value={4}>4x4</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Stride</label>
            <input
              type="number"
              value={layer.config.stride}
              onChange={(e) => updateConfig('stride', parseInt(e.target.value) || 1)}
              min="1"
              max="4"
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            />
          </div>
        </>
      );

    case 'dense':
      return (
        <>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Units</label>
            <input
              type="number"
              value={layer.config.units}
              onChange={(e) => updateConfig('units', parseInt(e.target.value) || 1)}
              min="1"
              max="2048"
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Activation</label>
            <select
              value={layer.config.activation}
              onChange={(e) => updateConfig('activation', e.target.value)}
              className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
            >
              <option value="RELU">ReLU</option>
              <option value="SIGMOID">Sigmoid</option>
              <option value="TANH">Tanh</option>
              <option value="SOFTMAX">Softmax</option>
            </select>
          </div>
        </>
      );

    case 'dropout':
      return (
        <div>
          <label className="block text-sm font-medium text-text-col-alt mb-1">
            Dropout Rate ({(layer.config.rate * 100).toFixed(0)}%)
          </label>
          <input
            type="range"
            min="0"
            max="0.9"
            step="0.1"
            value={layer.config.rate}
            onChange={(e) => updateConfig('rate', parseFloat(e.target.value))}
            className="w-full"
          />
        </div>
      );

    default:
      return null;
  }
}