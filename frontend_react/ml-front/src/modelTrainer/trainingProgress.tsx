import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { cnnTrainingSession } from "../services/constants";
import { useEffect, useState } from "react";

interface TrainingProgressProps {
  sessionStatus: cnnTrainingSession | null;
}

interface TrainingPointData {
  step: number;
  epoch: number | null;
  batch: number | null;
  loss: number | null;
  accuracy: number | null;
}

export function TrainingProgress({ sessionStatus }: TrainingProgressProps) {
  const [trainingData, setTrainingData] = useState<TrainingPointData[]>([]);

  useEffect(() => {
    setTrainingData([]);
  }, [sessionStatus?.sessionId]);

  useEffect(() => {
    if (!sessionStatus) return;

    const { currentEpoch, currentBatch, totalBatches, loss, accuracy } =
      sessionStatus;

    const step =
      (currentEpoch - 1) * totalBatches + currentBatch;

    setTrainingData(prev => {
      // prevent duplicates if the poll repeats the same batch
      if (prev.length && prev[prev.length - 1].step === step) {
        return prev;
      }

      return [
        ...prev,
        {
          step,
          epoch: currentEpoch,
          batch: currentBatch,
          loss,
          accuracy
        }
      ];
    });
  }, [sessionStatus?.currentEpoch, sessionStatus?.currentBatch, sessionStatus]);


  // useEffect(() => {
  //   if (!sessionStatus?.totalEpochs) return;
  //   console.log("Initializing training data for total epochs:", sessionStatus.totalEpochs);
  //   const initialData = Array.from(
  //     { length: sessionStatus.totalEpochs },
  //     (_, i) => ({
  //       epoch: i + 1,
  //       loss: null as number | null,
  //       accuracy: null as number | null
  //     })
  //   );

  //   setTrainingData(initialData);
  // }, [sessionStatus?.totalEpochs]);

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
              <LineChart data={trainingData} >
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis
                  dataKey="step"
                  stroke="#9CA3AF"
                  domain={[
                    1,
                    sessionStatus ? sessionStatus.totalEpochs * sessionStatus.totalBatches : 1
                  ]}
                  tick={false}
                />
                <YAxis yAxisId="left" stroke="#9CA3AF" domain={[0, 1]} />
                <YAxis yAxisId="right" orientation="right" stroke="#9CA3AF" domain={[0, 1]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1F2937',
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#9CA3AF'
                  }}
                  formatter={(value, name, props) => {
                    const { epoch, batch } = props.payload;
                    return [
                      value,
                      `${name} (epoch ${epoch}, batch ${batch})`
                    ];
                  }}
                />
                <Legend />
                <Line
                  yAxisId="left"
                  type="monotone"
                  dataKey="loss"
                  stroke="#EF4444"
                  strokeWidth={2}
                  connectNulls={false}
                  isAnimationActive={false}
                  name="Loss"
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="accuracy"
                  stroke="#10B981"
                  strokeWidth={2}
                  connectNulls={false}
                  isAnimationActive={false}
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