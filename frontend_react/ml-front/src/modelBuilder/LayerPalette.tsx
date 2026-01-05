import type { LayerType } from './types';

interface LayerPaletteProps {
  onDragStart: (layerType: LayerType) => void;
  onDragEnd: () => void;
}

const LAYER_DEFINITIONS = [
  { type: 'conv2d' as LayerType, name: 'Conv2D', icon: '⊞', color: 'bg-blue-600' },
  { type: 'maxpool' as LayerType, name: 'MaxPool', icon: '↓', color: 'bg-green-600' },
  { type: 'avgpool' as LayerType, name: 'AvgPool', icon: '≈', color: 'bg-green-500' },
  { type: 'flatten' as LayerType, name: 'Flatten', icon: '▤', color: 'bg-yellow-600' },
  { type: 'dense' as LayerType, name: 'Dense', icon: '●', color: 'bg-purple-600' },
  { type: 'dropout' as LayerType, name: 'Dropout', icon: '⚬', color: 'bg-red-600' }
];

export function LayerPalette({ onDragStart, onDragEnd }: LayerPaletteProps) {
  return (
    <div className="w-64 bg-bg-alt border-r border-border p-4">
      <h3 className="text-lg font-semibold text-text-col mb-4">Layer Palette</h3>

      <div className="space-y-2">
        {LAYER_DEFINITIONS.map(({ type, name, icon, color }) => (
          <div
            key={type}
            draggable
            onDragStart={() => onDragStart(type)}
            onDragEnd={onDragEnd}
            className={`${color} text-white p-3 rounded cursor-move hover:opacity-80 transition-opacity flex items-center gap-3`}
          >
            <span className="text-xl">{icon}</span>
            <div>
              <div className="font-medium">{name}</div>
              <div className="text-xs opacity-75">{getLayerDescription(type)}</div>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 p-3 bg-bg border border-border rounded">
        <h4 className="font-medium text-text-col mb-2">Instructions</h4>
        <ul className="text-sm text-text-col-alt space-y-1">
          <li>• Drag layers to canvas</li>
          <li>• Click to select & configure</li>
          <li>• Connect layers by dragging</li>
          <li>• Right-click to delete</li>
        </ul>
      </div>
    </div>
  );
}

function getLayerDescription(type: LayerType): string {
  switch (type) {
    case 'conv2d': return 'Feature extraction';
    case 'maxpool': return 'Spatial downsampling';
    case 'avgpool': return 'Average pooling';
    case 'flatten': return 'Reshape to 1D';
    case 'dense': return 'Fully connected';
    case 'dropout': return 'Regularization';
    default: return '';
  }
}