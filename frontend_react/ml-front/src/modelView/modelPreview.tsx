import type { CNNModel, Layer, LayerType } from "../modelBuilder/types";

interface ModelPreviewProps {
  model: CNNModel | null;
}

export function ModelPreview({ model }: ModelPreviewProps) {
  if (!model) {
    return (
      <div className="col-span-2 p-6 flex items-center justify-center">
        <div className="text-center text-text-col-alt">
          <div className="text-6xl mb-4">🔍</div>
          <h3 className="text-xl font-semibold mb-2">No Model Selected</h3>
          <p>Select a model to view its architecture</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col col-span-2 p-6">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-text-col mb-2">{model.name}</h2>
        <div className="flex gap-4 text-sm text-text-col-alt">
          <span>Input: {model.inputShape.join('×')}</span>
          <span>Output: {model.outputSize} classes</span>
          <span>Layers: {model.layers.length}</span>
        </div>
      </div>

      <div className="relative bg-bg border border-border rounded-lg overflow-hidden flex-1" >
        <div 
          className="w-full h-full relative"
          style={{
            backgroundImage: `radial-gradient(circle, #374151 1px, transparent 1px)`,
            backgroundSize: '20px 20px'
          }}
        >
          {/* Connection lines */}
          <svg className="absolute inset-0 pointer-events-none w-full h-full" style={{ zIndex: 1 }}>
            {model.layers.map(layer => 
              layer.connections.map(connId => {
                const targetLayer = model.layers.find(l => l.id === connId);
                if (!targetLayer) return null;

                const { start, end } = getConnectionPoints(layer, targetLayer);

                return (
                  <line
                    key={`${layer.id}-${connId}`}
                    x1={start.x}
                    y1={start.y}
                    x2={end.x}
                    y2={end.y}
                    stroke="#6B7280"
                    strokeWidth="2"
                    markerEnd="url(#arrowhead)"
                  />
                );
              })
            )}
            
            <defs>
              <marker
                id="arrowhead"
                markerWidth="10"
                markerHeight="7"
                refX="9"
                refY="3.5"
                orient="auto"
              >
                <polygon
                  points="0 0, 10 3.5, 0 7"
                  fill="#6B7280"
                />
              </marker>
            </defs>
          </svg>

          {/* Layer nodes */}
          {model.layers.map(layer => (
            <LayerPreviewNode key={layer.id} layer={layer} />
          ))}
        </div>
      </div>
    </div>
  );
}

function LayerPreviewNode({ layer }: { layer: Layer }) {
  const { icon, color, name } = getLayerVisuals(layer.type);

  return (
    <div
      className={`absolute w-32 h-16 rounded-lg border-2 border-gray-400 ${color}`}
      style={{
        left: layer.position.x,
        top: layer.position.y,
        userSelect: 'none'
      }}
    >
      <div className="flex flex-col items-center justify-center h-full text-white">
        <div className="text-lg">{icon}</div>
        <div className="text-xs font-medium">{name}</div>
        <div className="text-xs opacity-75">{getLayerInfo(layer)}</div>
      </div>
    </div>
  );
}

function getLayerVisuals(type: LayerType) {
  switch (type) {
    case 'conv2d':
      return { icon: '⊞', color: 'bg-blue-600', name: 'Conv2D' };
    case 'maxpool':
      return { icon: '↓', color: 'bg-green-600', name: 'MaxPool' };
    case 'avgpool':
      return { icon: '≈', color: 'bg-green-500', name: 'AvgPool' };
    case 'flatten':
      return { icon: '▤', color: 'bg-yellow-600', name: 'Flatten' };
    case 'dense':
      return { icon: '●', color: 'bg-purple-600', name: 'Dense' };
    case 'dropout':
      return { icon: '⚬', color: 'bg-red-600', name: 'Dropout' };
    default:
      return { icon: '?', color: 'bg-gray-600', name: 'Unknown' };
  }
}

function getLayerInfo(layer: Layer): string {
  switch (layer.type) {
    case 'conv2d':
      return `${layer.config.filters}@${layer.config.kernelSize}x${layer.config.kernelSize}`;
    case 'maxpool':
    case 'avgpool':
      return `${layer.config.poolSize}x${layer.config.poolSize}`;
    case 'dense':
      return `${layer.config.units} units`;
    case 'dropout':
      return `${(layer.config.rate * 100).toFixed(0)}%`;
    default:
      return '';
  }
}

function getConnectionPoints(fromLayer: Layer, toLayer: Layer) {
  const fromCenter = { x: fromLayer.position.x + 64, y: fromLayer.position.y + 32 };
  const toCenter = { x: toLayer.position.x + 64, y: toLayer.position.y + 32 };
  
  const dx = toCenter.x - fromCenter.x;
  const dy = toCenter.y - fromCenter.y;
  
  let fromSide: string, toSide: string;
  
  if (Math.abs(dx) > Math.abs(dy)) {
    fromSide = dx > 0 ? 'right' : 'left';
    toSide = dx > 0 ? 'left' : 'right';
  } else {
    fromSide = dy > 0 ? 'bottom' : 'top';
    toSide = dy > 0 ? 'top' : 'bottom';
  }
  
  const getPointForSide = (layer: Layer, side: string) => {
    const base = { x: layer.position.x, y: layer.position.y };
    switch (side) {
      case 'left': return { x: base.x, y: base.y + 32 };
      case 'right': return { x: base.x + 128, y: base.y + 32 };
      case 'top': return { x: base.x + 64, y: base.y };
      case 'bottom': return { x: base.x + 64, y: base.y + 64 };
      default: return { x: base.x + 64, y: base.y + 32 };
    }
  };
  
  return {
    start: getPointForSide(fromLayer, fromSide),
    end: getPointForSide(toLayer, toSide)
  };
}