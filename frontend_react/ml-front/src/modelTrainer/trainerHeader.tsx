import type { CNNModel } from "../modelBuilder/types";

interface TrainerHeaderProps {
  models: CNNModel[];
  selectedModelId: string;
  onModelChange: (modelId: string) => void;
  onModelStart: () => void;
  onTrainingStop: () => void;
  isRunning: boolean;
}

export function TrainerHeader({ models, selectedModelId, onModelChange, onModelStart,onTrainingStop, isRunning }: TrainerHeaderProps) {
  return (
    <header className="flex items-center justify-between px-6 py-4 border-b border-border bg-bg-alt">
      <h1 className="text-xl font-semibold text-text-col">Model Training</h1>

      <div className="flex items-center gap-4">
        <select
          value={selectedModelId}
          onChange={(e) => onModelChange(e.target.value)}
          className="border border-border rounded-md px-3 py-2 text-sm bg-bg text-text-col"
          disabled={isRunning}
        >
          {models.length === 0 ? (
            <option value="">No models available</option>
          ) : (
            models.map((model) => (
              <option key={model.modelId} value={model.modelId || ""}>
                {model.name}
              </option>
            ))
          )}
        </select>

        <button
          className="btn-acc text-sm"
          onClick={onModelStart}
          disabled={isRunning || !selectedModelId}
        >
          {isRunning ? "Training..." : "Start"}
        </button>
        <button
          className="btn-sec text-sm"
          disabled
        >
          Pause
        </button>
        <button
          className="btn text-sm"
          onClick={onTrainingStop}
          disabled={!isRunning}
        >
          Stop
        </button>
      </div>
    </header>
  );
}