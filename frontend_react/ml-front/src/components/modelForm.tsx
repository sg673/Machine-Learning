import React from "react";
import { api } from "../services/api";

interface ModelFormProps {
    isOpen:boolean;
    onClose: () => void;
}

export function ModelForm({isOpen,onClose}:ModelFormProps){
    const [sessionId, setSessionId] = React.useState<string>("");
    const handleSubmit = async (event:React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        
        const formData = new FormData(event.currentTarget);
        const model = {
            modelName: formData.get('modelName') as string,
            trainingData: formData.get('trainingData') as string,
            epochs: parseInt(formData.get('epochs') as string || '0'),
            batchSize: parseInt(formData.get('batchSize') as string || '0'),
            learningRate: parseFloat(formData.get('learningRate') as string || '0'),
            layers: formData.get('layers') as string,
            activationFunction: formData.get('activationFunction') as string
        };
        try{
            const response = await api.startTraining(model);
            setSessionId(response.sessionId);
            console.log(response);
            
            const update = await api.getTrainingStatus(response.sessionId);
            console.group("Training Status Update");
            console.log("Checking status for session:", update);
            console.groupEnd();
            onClose();
            

        }
        catch(error){
            console.error("Error creating model:", error);
        }

    }
    if(!isOpen) return null;
    return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-bg-alt border border-border rounded-lg p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold text-text-col">Create New Model</h2>
          <button onClick={onClose} className="text-text-col-alt hover:text-text-col">✕</button>
        </div>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Model Name</label>
            <input type="text" name="modelName" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Training Data</label>
            <select name="trainingData" className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt">
              <option value="MNIST">MNIST</option>
            </select>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Epochs</label>
            <input type="number" name="epochs" min="1" max="1000" defaultValue="10" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Batch Size</label>
            <input type="number" name="batchSize" min="1" max="1024" defaultValue="32" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Learning Rate</label>
            <input type="number" name="learningRate" step="0.001" min="0.001" max="1.0" defaultValue="0.001" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Layers</label>
            <input type="text" name="layers" placeholder="e.g. 64,32,10" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Activation Function</label>
            <select name="activationFunction" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt">
              <option value="RELU">ReLU</option>
              <option value="SIGMOID">Sigmoid</option>
              <option value="TANH">Tanh</option>
            </select>
          </div>
          
          <div className="flex gap-3 pt-4">
            <button type="button" onClick={onClose} className="flex-1 px-4 py-2 border border-border rounded text-text-col-alt hover:bg-bg">Cancel</button>
            <button type="submit" className="flex-1 btn">Create Model</button>
          </div>
        </form>
      </div>
    </div>
  );
}