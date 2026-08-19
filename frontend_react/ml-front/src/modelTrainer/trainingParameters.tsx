interface TrainingParametersProps {
  epochs: number;
  lr: number;
  batchSize: number;
  onEpochsChange: (epochs: number) => void;
  onLrChange: (lr: number) => void;
  onBatchSizeChange: (batchSize: number) => void;
}

export function TrainingParameters({ 
  epochs, 
  lr, 
  batchSize, 
  onEpochsChange, 
  onLrChange, 
  onBatchSizeChange 
}: TrainingParametersProps) {
  return (
    <aside className="w-80 border-r border-border bg-bg-alt p-6 overflow-y-auto">
      <h2 className="text-lg font-medium mb-4 text-text-col">Training Parameters</h2>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1 text-text-col-alt">Epochs</label>
          <input
            type="number"
            value={epochs}
            onChange={(e) => onEpochsChange(Number(e.target.value))}
            className="w-full border border-border rounded-md px-3 py-2 text-sm bg-bg text-text-col"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1 text-text-col-alt">Learning Rate</label>
          <input
            type="number"
            step="0.0001"
            value={lr}
            onChange={(e) => onLrChange(Number(e.target.value))}
            className="w-full border border-border rounded-md px-3 py-2 text-sm bg-bg text-text-col"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1 text-text-col-alt">Batch Size</label>
          <input
            type="number"
            value={batchSize}
            onChange={(e) => onBatchSizeChange(Number(e.target.value))}
            className="w-full border border-border rounded-md px-3 py-2 text-sm bg-bg text-text-col"
          />
        </div>

        <button className="btn w-full mt-4 text-sm">
          Save Configuration
        </button>
      </div>
    </aside>
  );
}