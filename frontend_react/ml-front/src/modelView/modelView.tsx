import { useState } from "react";
import { ModelList } from "./modelList";
import { ModelPreview } from "./modelPreview";
import { ModelProperties } from "./modelProperties";
import type { CNNModel } from "../modelBuilder/types";


export function ModelView() {
  const [selectedModel, setSelectedModel] = useState<CNNModel | null>(null);

  const handleSelectModel = (model: CNNModel) => {
    setSelectedModel(model);
  };

  return (
    <div className="h-screen grid grid-cols-4 bg-bg">
      <ModelList onSelectModel={handleSelectModel}/>
      <ModelPreview model={selectedModel}/>
      <ModelProperties />
    </div>
  );
}