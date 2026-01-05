import React, { useState, useCallback } from 'react';
import type { Layer, LayerType } from './types';

interface LayerNodeProps {
  layer: Layer;
  isSelected: boolean;
  isConnecting: boolean;
  onClick: (e: React.MouseEvent) => void;
  onDoubleClick: () => void;
  onRightClick: (e: React.MouseEvent) => void;
  onDrag: (position: { x: number; y: number }) => void;
}

export function LayerNode({ 
  layer, 
  isSelected, 
  isConnecting, 
  onClick, 
  onDoubleClick, 
  onRightClick, 
  onDrag 
}: LayerNodeProps) {
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 });

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    if (e.button === 0) { // Left click only
      setIsDragging(true);
      setDragStart({
        x: e.clientX - layer.position.x,
        y: e.clientY - layer.position.y
      });
    }
  }, [layer.position]);

  const handleMouseMove = useCallback((e: MouseEvent) => {
    if (isDragging) {
      onDrag({
        x: e.clientX - dragStart.x,
        y: e.clientY - dragStart.y
      });
    }
  }, [isDragging, dragStart, onDrag]);

  const handleMouseUp = useCallback(() => {
    setIsDragging(false);
  }, []);

  // Attach global mouse events for dragging
  React.useEffect(() => {
    if (isDragging) {
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
      return () => {
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
      };
    }
  }, [isDragging, handleMouseMove, handleMouseUp]);

  const { icon, color, name } = getLayerVisuals(layer.type);

  return (
    <div
      className={`
        absolute w-32 h-16 rounded-lg border-2 cursor-pointer transition-all
        ${isSelected ? 'border-blue-400 shadow-lg' : 'border-gray-400'}
        ${isConnecting ? 'ring-2 ring-blue-400 ring-opacity-50' : ''}
        ${isDragging ? 'z-50' : 'z-10'}
        ${color}
      `}
      style={{
        left: layer.position.x,
        top: layer.position.y,
        userSelect: 'none'
      }}
      onClick={onClick}
      onDoubleClick={onDoubleClick}
      onContextMenu={onRightClick}
      onMouseDown={handleMouseDown}
    >
      <div className="flex flex-col items-center justify-center h-full text-white">
        <div className="text-lg">{icon}</div>
        <div className="text-xs font-medium">{name}</div>
        <div className="text-xs opacity-75">{getLayerInfo(layer)}</div>
      </div>
      
      {/* Connection points */}
      <div className="absolute -left-1 top-1/2 w-2 h-2 bg-gray-400 rounded-full transform -translate-y-1/2" />
      <div className="absolute -right-1 top-1/2 w-2 h-2 bg-gray-400 rounded-full transform -translate-y-1/2" />
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