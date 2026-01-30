import type { cnnTrainingSession } from "../services/constants";

interface TrainingProgressProps {
  sessionStatus: cnnTrainingSession | null;
}

export function TrainingProgress({sessionStatus}: TrainingProgressProps){
  return (
    <div className="flex-1 p-6 overflow-y-auto">
      <p>Progress graph here</p>
    </div>
  );
}