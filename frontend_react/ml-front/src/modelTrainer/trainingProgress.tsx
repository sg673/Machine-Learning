import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { cnnTrainingSession } from "../services/constants";

interface TrainingProgressProps {
  sessionStatus: cnnTrainingSession | null;
}

export function TrainingProgress({ sessionStatus }: TrainingProgressProps) {
  // Generate mock data based on current training progress
  const generateTrainingData = () => {
    if (!sessionStatus) return [];
    
    const data = [];
    for (let i = 1; i <= sessionStatus.currentEpoch; i++) {
      data.push({
        epoch: i,
        loss: Math.max(0.1, 1 - (i / sessionStatus.totalEpochs) * 0.8 + Math.random() * 0.1),
        accuracy: Math.min(0.95, (i / sessionStatus.totalEpochs) * 0.85 + Math.random() * 0.1)
      });
    }
    return data;
  };

  const trainingData = generateTrainingData();

  return (
    <section className="flex-1 p-6">
      <h2 className="text-lg font-medium mb-4 text-text-col">Training Progress</h2>
      
      {sessionStatus ? (
        <div className="h-full">
          <div className="mb-4 grid grid-cols-2 gap-4 text-sm">
            <div className="text-text-col-alt">
              Status: <span className="text-text-col">{sessionStatus.status}</span>
            </div>
            <div className="text-text-col-alt">
              Epoch: <span className="text-text-col">{sessionStatus.currentEpoch}/{sessionStatus.totalEpochs}</span>
            </div>
            <div className="text-text-col-alt">
              Batch: <span className="text-text-col">{sessionStatus.currentBatch}/{sessionStatus.totalBatches}</span>
            </div>
            <div className="text-text-col-alt">
              Accuracy: <span className="text-text-col">{(sessionStatus.accuracy * 100).toFixed(2)}%</span>
            </div>
          </div>
          
          <div className="h-96 rounded-xl border border-border bg-bg-alt p-4">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={trainingData} width="100%" height="100%">
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="epoch" stroke="#9CA3AF" />
                <YAxis yAxisId="left" stroke="#9CA3AF" />
                <YAxis yAxisId="right" orientation="right" stroke="#9CA3AF" />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#1F2937', 
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#9CA3AF'
                  }} 
                />
                <Legend />
                <Line 
                  yAxisId="left" 
                  type="monotone" 
                  dataKey="loss" 
                  stroke="#EF4444" 
                  strokeWidth={2}
                  name="Loss"
                />
                <Line 
                  yAxisId="right" 
                  type="monotone" 
                  dataKey="accuracy" 
                  stroke="#10B981" 
                  strokeWidth={2}
                  name="Accuracy"
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      ) : (
        <div className="h-full rounded-xl border border-border bg-bg-alt flex items-center justify-center text-text-col-alt">
          No active training session
        </div>
      )}
    </section>
  );
} 