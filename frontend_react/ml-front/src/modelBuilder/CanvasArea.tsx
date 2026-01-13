import { forwardRef, useCallback, useState } from 'react';
import type { Layer, LayerType } from './types';
import { LayerNode } from './LayerNode';

interface CanvasAreaProps {
  layers: Layer[];
  selectedLayer: Layer | null;
  draggedLayer: LayerType | null;
  onLayerSelect: (layer: Layer | null) => void;
  onLayerAdd: (layerType: LayerType, position: { x: number; y: number }) => void;
  onLayerUpdate: (layerId: string, updates: Partial<Layer>) => void;
  onLayerDelete: (layerId: string) => void;
  onLayerConnect: (fromId: string, toId: string) => void;
}

export const CanvasArea = forwardRef<HTMLDivElement, CanvasAreaProps>(
  ({ layers, selectedLayer, draggedLayer, onLayerSelect, onLayerAdd, onLayerUpdate, onLayerDelete, onLayerConnect }, ref) => {
    const [connecting, setConnecting] = useState<string | null>(null);

    const handleDrop = useCallback((e: React.DragEvent) => {
      e.preventDefault();
      if (!draggedLayer) return;

      const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
      const position = {
        x: e.clientX - rect.left - 60, // Center the node
        y: e.clientY - rect.top - 30
      };

      onLayerAdd(draggedLayer, position);
    }, [draggedLayer, onLayerAdd]);

    const handleDragOver = useCallback((e: React.DragEvent) => {
      e.preventDefault();
    }, []);

    const handleCanvasClick = useCallback((e: React.MouseEvent) => {
      if (e.target === e.currentTarget) {
        onLayerSelect(null);
        setConnecting(null);
      }
    }, [onLayerSelect]);

    const handleLayerClick = useCallback((layer: Layer, e: React.MouseEvent) => {
      e.stopPropagation();
      
      if (connecting) {
        if (connecting !== layer.id) {
          onLayerConnect(connecting, layer.id);
        }
        setConnecting(null);
      } else {
        onLayerSelect(layer);
      }
    }, [connecting, onLayerConnect, onLayerSelect]);

    const handleLayerDoubleClick = useCallback((layer: Layer) => {
      setConnecting(layer.id);
    }, []);

    const handleLayerRightClick = useCallback((layer: Layer, e: React.MouseEvent) => {
      e.preventDefault();
      onLayerDelete(layer.id);
    }, [onLayerDelete]);

    const handleLayerDrag = useCallback((layerId: string, position: { x: number; y: number }) => {
      onLayerUpdate(layerId, { position });
    }, [onLayerUpdate]);

    return (
      <div
        ref={ref}
        className="flex-1 relative bg-bg overflow-hidden"
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onClick={handleCanvasClick}
        style={{
          backgroundImage: `
            radial-gradient(circle, #374151 1px, transparent 1px)
          `,
          backgroundSize: '20px 20px'
        }}
      >
        {/* Connection lines */}
        <svg className="absolute inset-0 pointer-events-none w-full h-full" style={{ zIndex: 1 }}>
          {layers.map(layer => 
            layer.connections.map(connId => {
              const targetLayer = layers.find(l => l.id === connId);
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
        {layers.map(layer => (
          <LayerNode
            key={layer.id}
            layer={layer}
            isSelected={selectedLayer?.id === layer.id}
            isConnecting={connecting === layer.id}
            onClick={(e) => handleLayerClick(layer, e)}
            onDoubleClick={() => handleLayerDoubleClick(layer)}
            onRightClick={(e) => handleLayerRightClick(layer, e)}
            onDrag={(position) => handleLayerDrag(layer.id, position)}
          />
        ))}

        {/* Instructions overlay */}
        {layers.length === 0 && (
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="text-center text-text-col-alt">
              <div className="text-6xl mb-4">🧠</div>
              <h3 className="text-xl font-semibold mb-2">Build Your CNN Model</h3>
              <p>Drag layers from the palette to start building</p>
            </div>
          </div>
        )}

        {connecting && (
          <div className="absolute top-4 left-4 bg-blue-600 text-white px-3 py-2 rounded">
            Click another layer to connect
          </div>
        )}
      </div>
    );
  }
);

function getConnectionPoints(fromLayer: Layer, toLayer: Layer) {
  const fromCenter = { x: fromLayer.position.x + 64, y: fromLayer.position.y + 32 };
  const toCenter = { x: toLayer.position.x + 64, y: toLayer.position.y + 32 };
  
  const dx = toCenter.x - fromCenter.x;
  const dy = toCenter.y - fromCenter.y;
  
  // Determine which sides to connect based on relative positions
  let fromSide: string, toSide: string;
  
  if (Math.abs(dx) > Math.abs(dy)) {
    // Horizontal connection
    fromSide = dx > 0 ? 'right' : 'left';
    toSide = dx > 0 ? 'left' : 'right';
  } else {
    // Vertical connection
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