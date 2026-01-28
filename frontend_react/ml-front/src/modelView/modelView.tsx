import { useState } from "react";
import { ModelList } from "./modelList";
import { ModelPreview } from "./modelPreview";
import { ModelProperties } from "./modelProperties";
import type { CNNModel } from "../modelBuilder/types";
import { cnnModelApi } from "../services/api";
import { ModelBuilder } from "../modelBuilder";

interface ModelViewProps {
  onPageChange?: (page: string) => void;
}

export function ModelView({ onPageChange }: ModelViewProps) {
  const [selectedModel, setSelectedModel] = useState<CNNModel | null>(null);
  const [editingModel, setEditingModel] = useState<CNNModel | null>(null);

  const handleSelectModel = (model: CNNModel) => {
    setSelectedModel(model);
  };

  const handleEditModel = (model: CNNModel) => {
    setEditingModel(model);
  };
  
  const handleModelSaved = () => {
    setEditingModel(null);
    onPageChange?.("modelBuilder");
    // Refresh model list if needed
  };

  const handleDeleteModel = (modelId: string) => {
    cnnModelApi.delete(modelId);
    if (selectedModel?.modelId === modelId) {
      setSelectedModel(null);
    }
    // Implement handler logic
  };

  const handleTrainModel = (modelId: string) => {
    // Implement train model functionality
  };

  if (editingModel) {
    return (
      <ModelBuilder
        existingModel={editingModel}
        onModelSaved={handleModelSaved}
      />
    )
  }

  return (
    <div className="h-screen grid grid-cols-4 bg-bg">
      <ModelList
        onSelectModel={handleSelectModel}
        onEditModel={handleEditModel}
        onDeleteModel={handleDeleteModel}
        onTrainModel={handleTrainModel}
      />
      <ModelPreview model={selectedModel} />
      <ModelProperties
        model={selectedModel}
        onEditModel={() => selectedModel && handleEditModel(selectedModel)}
        onDeleteModel={handleDeleteModel}
        onTrainModel={handleTrainModel}
      />
    </div>
  );
}