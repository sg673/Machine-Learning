import { StatsCard } from '../components/StatsCard';
import { useState } from 'react';
import { ModelForm } from '../components/modelForm';
import robotIcon from '../assets/robot.svg';
import lightningIcon from '../assets/lightning-filled.svg';
import checkmarkIcon from '../assets/checkmark-circle.svg';
import chartIcon from '../assets/baseline-bar-chart.svg';

export function Dashboard() {
    const [isModelFormOpen, setIsModelFormOpen] = useState(false);
  return (
    <div className="p-6 bg-bg min-h-screen">
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <StatsCard title="Models Trained" value="12" icon={robotIcon} />
        <StatsCard title="Active Jobs" value="2" icon={lightningIcon} />
        <StatsCard title="Success Rate" value="94%" icon={checkmarkIcon} />
        <StatsCard title="Avg Accuracy" value="87.3%" icon={chartIcon} />
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        {/* Model Training */}
        <div className="card">
          <h3 className="text-xl font-bold mb-4">Model Training</h3>
          <div className="space-y-4 justify-center">
            <button 
                className="btn w-full"
                onClick={() => setIsModelFormOpen(true)}
            >
              New Model
            </button>
            <button className="btn-sec w-full">
              Load Model
            </button>
            <button className="btn-acc w-full">
              Start Training
            </button>
          </div>
        </div>

        {/* Recent Models */}
        <div className="card">
          <h3 className="text-xl font-bold mb-4">Recent Models</h3>
          <div className="space-y-3">
            <div className="flex justify-between items-center p-3 bg-bg rounded border border-border">
              <span className="text-sm text-text-col-alt">sample-2025-09-04</span>
              <a href="#" className="text-prim hover:text-prim-hover">View</a>
            </div>
            <div className="flex justify-between items-center p-3 bg-bg rounded border border-border">
              <span className="text-sm text-text-col-alt">t1-2025-09-04</span>
              <a href="#" className="text-prim hover:text-prim-hover">View</a>
            </div>
          </div>
        </div>
      </div>

      {/* Training Progress */}
      <div className="card">
        <h3 className="text-xl font-bold mb-4">Training Progress & Results</h3>
        <div className="h-64 bg-bg rounded border border-border flex items-center justify-center">
            <ModelForm isOpen={isModelFormOpen} onClose={()=>setIsModelFormOpen(false)}/>
        </div>
      </div>
    </div>
  );
}