import { useState } from "react";
import { ModelList } from "./modelList";
import { ModelPreview } from "./modelPreview";
import { ModelProperties } from "./modelProperties";
import type { CNNModel } from "../modelBuilder/types";
import { cnnModelApi } from "../services/api";


export function ModelView() {
  const [selectedModel, setSelectedModel] = useState<CNNModel | null>(null);

  const handleSelectModel = (model: CNNModel) => {
    setSelectedModel(model);
  };

  const handleEditModel = () => {
    // Implement edit model functionality
  };

  const handleDeleteModel = (modelId: string) => {
    cnnModelApi.delete(modelId);
    // Implement handler logic
  };

  const handleTrainModel = (modelId: string) => {
    // Implement train model functionality
  };

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
        onEditModel={handleEditModel}
        onDeleteModel={handleDeleteModel}
        onTrainModel={handleTrainModel}
      />
    </div>
  );
}