import { api } from "../services/api";

export function ModelForm(){
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
            const response = await api.createModel(model);
            console.log(response);
        }
        catch(error){
            console.error("Error creating model:", error);
        }

    }
    return (
        <div>
            <form onSubmit={handleSubmit}>
                <label>
                    Model Name:
                    <input type="text" name="modelName" required />
                </label>
                <label>
                    Training data:
                    <select name="trainingData">
                        <option value="MNIST">MNIST</option>
                    </select>
                </label>
                <label>
                    Epochs:
                    <input type="number" name="epochs" min="1" max="1000" defaultValue="10" required />
                </label>
                <label>
                    Batch Size:
                    <input type="number" name="batchSize" min="1" max="1024" defaultValue="32" required />
                </label>
                <label>
                    Learning Rate:
                    <input type="number" name="learningRate" step="0.0001" min="0.0001" max="1.0" defaultValue="0.001" required />
                </label>
                <label>
                    Layers:
                    <input type="text" name="layers" placeholder="e.g. 64,32,10" required />
                </label>
                <label>
                    Activation Function:
                    <select name="activationFunction" required>
                        <option value="RELU">ReLU</option>
                        <option value="SIGMOID">Sigmoid</option>
                        <option value="TANH">Tanh</option>
                    </select>
                </label>
                <button type="submit">Create Model</button>
            </form>
        </div>
    );
}