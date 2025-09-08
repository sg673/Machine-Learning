# Feed Forward Neural Network (FFN)

A full-stack neural network framework built with Java Spring Boot backend and React TypeScript frontend for training and visualizing feed-forward neural networks on the MNIST dataset.

## Features

- **Custom Neural Network Implementation**: Built-from-scratch feed-forward network with configurable layers
- **MNIST Dataset Support**: Integrated MNIST data loader for handwritten digit recognition
- **Multiple Activation Functions**: Support for ReLU, Sigmoid, and other activation functions
- **Model Persistence**: Save and load trained models in custom `.fnn` format
- **REST API**: Spring Boot backend with RESTful endpoints
- **Modern Frontend**: React TypeScript interface with Tailwind CSS styling
- **Real-time Training**: Monitor training progress and model performance

## Prerequisites

- **Java 17+**
- **Node.js 18+**
- **npm or yarn**

## Quick Start

### Backend Setup

1. Navigate to the backend directory:
```bash
cd fnn
```

2. Run the Spring Boot application:
```bash
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend_react/ml-front
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

## API Endpoints

Base URL: `http://localhost:8080/api/v1`

### Dashboard
- `GET /dashboard/stats` - Get dashboard statistics (total models, active sessions, accuracy metrics)

### Models
- `GET /models` - List all models
- `POST /models` - Create a new model
- `GET /models/{modelId}` - Get model details by ID
- `DELETE /models/{modelId}` - Delete a model

### Training
- `POST /training/start` - Start model training session
- `GET /training/{sessionId}/status` - Get real-time training status
- `POST /training/{sessionId}/stop` - Stop active training session

### Results
- `GET /results` - Get training results (supports filtering by modelId and limit)
- `GET /results/{resultId}` - Get specific training result details

### Settings
- `GET /settings` - Get application settings
- `PUT /settings` - Update application settings

#### Model Creation Example
```json
{
  "name": "MNIST Classifier",
  "type": "FFN",
  "architecture": {
    "inputSize": 784,
    "hiddenLayers": [128, 64],
    "outputSize": 10,
    "activationFunction": "RELU"
  }
}
```

#### Training Request Example
```json
{
  "modelId": "model-123",
  "dataset": "MNIST",
  "epochs": 100,
  "learningRate": 0.001,
  "batchSize": 32
}
```

## Neural Network Architecture

The framework supports:
- **Input Layer**: Configurable size based on input data dimensions
- **Hidden Layers**: Configurable size and count
- **Output Layer**: Configurable size based on number of classes
- **Activation Functions**: ReLU, Sigmoid, Tanh
- **Training Algorithm**: Backpropagation with gradient descent

## Dataset Support

The framework is designed to work with various datasets. MNIST example:
- **Input**: 784 neurons (28x28 grayscale images)
- **Output**: 10 classes (digits 0-9)
- **Training samples**: 60,000
- **Test samples**: 10,000

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.5.5
- Gradle
- Gson for JSON processing

### Frontend
- React 19
- TypeScript
- Tailwind CSS
- Vite

## Development

### Building the Backend
```bash
cd fnn
./gradlew build
```

### Building the Frontend
```bash
cd frontend_react/ml-front
npm run build
```

## License

This project is part of a portfolio demonstration.