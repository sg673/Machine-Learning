import {useState} from "react";
import { api } from "../services/api";
import { DATASET_CONFIG } from "../services/constants";

interface ModelFormProps {
    isOpen:boolean;
    onClose: () => void;
    onSessionCreated: (sessionId:string) => void;
}

export function ModelForm({isOpen, onClose, onSessionCreated}:ModelFormProps){
    const [selectedDataset, setSelectedDataset] = useState<string>("MNIST");

    const handleSubmit = async (event:React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        
        const formData = new FormData(event.currentTarget);
        const hiddenLayers = formData.get('hiddenLayers') as string;
        const config = DATASET_CONFIG[selectedDataset as keyof typeof DATASET_CONFIG];

        const layers = hiddenLayers ? 
            `${config.inputSize},${hiddenLayers},${config.outputSize}` : 
            `${config.inputSize},${config.outputSize}`;

        const model = {
            modelName: formData.get('modelName') as string,
            trainingData: formData.get('trainingData') as string,
            epochs: parseInt(formData.get('epochs') as string || '0'),
            batchSize: parseInt(formData.get('batchSize') as string || '0'),
            learningRate: parseFloat(formData.get('learningRate') as string || '0'),
            layers,
            activationFunction: formData.get('activationFunction') as string
        };
        try{
            const response = await api.startTraining(model);
            onSessionCreated(response.sessionId);
            console.log(response);
            
            onClose();
            

        }
        catch(error){
            console.error("Error creating model:", error);
        }

    }
    if(!isOpen) return null;
    const config = DATASET_CONFIG[selectedDataset as keyof typeof DATASET_CONFIG];
    return (
    <div className="fixed inset-0 bg-bg opacity-100 flex items-center justify-center z-50">
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
            <select name="trainingData"
                className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt"
                value={selectedDataset}
                onChange={(e) => setSelectedDataset(e.target.value)}    
            >
              <option value="MNIST">MNIST</option>
            </select>
            <p className="text-xs text-text-col mt-1">
                Input Size: {config.inputSize}, Output Size: {config.outputSize}
            </p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Epochs</label>
            <input type="number" name="epochs" 
                min="1" max="1000" defaultValue="10" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Batch Size</label>
            <input type="number" name="batchSize" 
                min="1" max="1024" defaultValue="32" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Learning Rate</label>
            <input type="number" name="learningRate" step="0.001" 
                min="0.001" max="1.0" defaultValue="0.001" required className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-text-col-alt mb-1">Hidden Layers</label>
            <input type="text" name="hiddenLayers" 
                placeholder="e.g. 128,64 (optional)"
                 className="w-full px-3 py-2 bg-bg border border-border rounded text-text-col-alt" 
            />
            <p className="text-xs text-text-col mt-1">
                Comma-separated sizes for hidden layers. Leave blank for direct input to output connection
            </p>
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