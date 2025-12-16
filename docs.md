# Technical Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [Project Structure](#project-structure)
3. [Backend (Java Spring Boot)](#backend-java-spring-boot---fnn)
   - [Core Application Files](#core-application-files)
   - [Controllers](#controllers-srcmainjavacomportfolionncontroller)
   - [Neural Network Core](#neural-network-core-srcmainjavacomportfolionnnetwork)
   - [Layer Architecture](#layer-architecture-srcmainjavacomportfolionnnetworklayers)
   - [Loss Functions](#loss-functions-srcmainjavacomportfolionnnetworkloss)
   - [Data Management](#data-management-srcmainjavacomportfolionndata)
   - [Models](#models-srcmainjavacomportfolionnmodel)
   - [Services](#services-srcmainjavacomportfolionnservice)
   - [Utilities](#utilities-srcmainjavacomportfolionnutil)
   - [Resources](#resources-srcmainresources)
4. [Frontend (React TypeScript)](#frontend-react-typescript---frontend_reactml-front)
   - [Core Application Files](#core-application-files-1)
   - [Components](#components-srccomponents)
   - [Dashboard](#dashboard-srcdashboard)
   - [Services](#services-srcservices)
   - [Assets](#assets-srcassets)
   - [Configuration Files](#configuration-files)
5. [Neural Network Architectures](#neural-network-architectures)
6. [Data Flow Architecture](#data-flow-architecture)
7. [Key Design Patterns](#key-design-patterns)
8. [File Extensions and Formats](#file-extensions-and-formats)
9. [Development Workflow](#development-workflow)
10. [API Integration](#api-integration)

## Project Overview

This is a comprehensive full-stack neural network framework featuring both **Feed-Forward Neural Networks (FFN)** and **Convolutional Neural Networks (CNN)** implementations. Built with Java Spring Boot backend and React TypeScript frontend, it provides a complete platform for training, evaluating, and visualizing deep learning models on the MNIST dataset and other image classification tasks.

## Project Structure

```
ffn/
├── fnn/                          # Java Spring Boot Backend
├── frontend_react/ml-front/      # React TypeScript Frontend  
├── savedModels/                  # Saved model files (.json format)
├── README.md                     # Project overview and setup
└── docs.md                       # This documentation file
```

---

## Backend (Java Spring Boot) - `/fnn/`

### Core Application Files

#### `src/main/java/com/portfolio/nn/NeuralNetworkApplication.java`
- **Purpose**: Main Spring Boot application entry point
- **Functionality**: 
  - Starts the Spring Boot server
  - Configures the application context

#### `build.gradle`
- **Purpose**: Gradle build configuration
- **Dependencies**:
  - Spring Boot Web & JPA
  - Gson for JSON processing
  - H2 database for model persistence
  - JUnit for testing

### Controllers (`src/main/java/com/portfolio/nn/controller/`)

#### `ModelController.java`
- **Purpose**: REST API endpoints for model management
- **Endpoints**:
  - `GET /api/v1/models` - List all models
  - `POST /api/v1/models` - Create new model
  - `GET /api/v1/models/{id}` - Get model by ID
  - `DELETE /api/v1/models/{id}` - Delete model
- **CORS**: Configured for `http://localhost:5173` (React dev server)

#### `TrainingController.java`
- **Purpose**: Handles training session management
- **Endpoints**:
  - `POST /api/v1/training/start` - Start training
  - `GET /api/v1/training/{sessionId}/status` - Get training status
  - `POST /api/v1/training/{sessionId}/stop` - Stop training

#### `ResultController.java`
- **Purpose**: Manages training results and metrics
- **Endpoints**:
  - `GET /api/v1/results` - Get all results
  - `GET /api/v1/results/{resultId}` - Get specific result

#### `HomeController.java`
- **Purpose**: Dashboard and general application endpoints
- **Endpoints**:
  - `GET /api/v1/dashboard/stats` - Dashboard statistics

### Neural Network Core (`src/main/java/com/portfolio/nn/network/`)

#### `FeedForwardNetwork.java`
- **Purpose**: Traditional fully-connected neural network implementation
- **Features**:
  - Forward propagation with configurable dense layers
  - Backpropagation with mini-batch gradient descent
  - Model serialization/deserialization (JSON format)
  - Training progress tracking via `TrainingSession`
  - Support for multiple activation functions
  - Optimized for tabular data and simple classification tasks

#### `ConvolutionalNetwork.java`
- **Purpose**: Advanced CNN implementation for image processing
- **Features**:
  - **Modular Layer Architecture**: Linked-list based layer composition
  - **3D Tensor Processing**: Handles depth, height, width dimensions
  - **Flexible Layer Stacking**: Supports any combination of Conv, Pooling, and FC layers
  - **Automatic Shape Inference**: Calculates output dimensions automatically
  - **Configurable Loss Functions**: Pluggable loss function system
  - **Memory Efficient**: Optimized tensor operations with parallel processing

#### `NeuralNetworkBase.java`
- **Purpose**: Common interface for all neural network types
- **Methods**:
  - `forward(double[] input)` - Forward pass returning predictions
  - `predict(double[] input)` - Classification prediction (returns class index)
  - `train(double[][] x, double[][] y, double learningRate, int epochs)` - Training method
  - `evaluate(double[][] testX, double[][] testY)` - Model evaluation returning accuracy

#### `activation/ActivationFunction.java`
- **Purpose**: Activation function implementations with mathematical derivatives
- **Supported Functions**:
  - **ReLU**: `f(x) = max(0, x)` - Best for hidden layers, prevents vanishing gradients
  - **Sigmoid**: `f(x) = 1/(1 + e^(-x))` - Output layer for binary classification
  - **Tanh**: `f(x) = tanh(x)` - Alternative to sigmoid with zero-centered output
- **Features**: 
  - Efficient activation and derivative calculations
  - Thread-safe implementations
  - Numerical stability optimizations

### Layer Architecture (`src/main/java/com/portfolio/nn/network/layers/`)

#### `LayerBase.java`
- **Purpose**: Abstract base class for all neural network layers
- **Architecture**: 
  - **Linked List Structure**: Each layer maintains references to previous/next layers
  - **3D Tensor Interface**: All layers process `double[depth][height][width]` tensors
  - **Automatic Shape Management**: Input/output dimensions calculated automatically
  - **Memory Optimization**: Caches last input/output for efficient backpropagation
- **Key Methods**:
  - `forward(double[][][] input)` - Forward propagation
  - `backward(double[][][] gradient, double learningRate)` - Backpropagation
  - `setInputShape(int width, int height, int depth)` - Configure layer dimensions

#### `ConvolutionalLayer.java`
- **Purpose**: Convolutional layer for feature extraction from images
- **Features**:
  - **Configurable Filters**: Custom filter count, size, stride, and padding
  - **Parallel Processing**: Multi-threaded convolution operations using Java Streams
  - **Im2Col Optimization**: Efficient matrix multiplication approach for convolutions
  - **Memory Management**: Synchronized weight updates for thread safety
- **Parameters**:
  - `filterCount` - Number of feature maps to generate
  - `filterSize` - Spatial dimensions of convolution kernels
  - `stride` - Step size for filter movement
  - `padding` - Zero-padding for border handling
- **Mathematical Operations**:
  - Forward: Convolution + bias + activation
  - Backward: Gradient computation for filters, biases, and input

#### `PoolingLayer.java`
- **Purpose**: Downsampling layer for dimensionality reduction and translation invariance
- **Pooling Types**:
  - **Max Pooling**: Preserves dominant features, better for edge detection
  - **Average Pooling**: Captures general patterns, smoother feature maps
- **Features**:
  - **Configurable Pool Size**: Typically 2x2 for standard downsampling
  - **Stride Control**: Usually matches pool size for non-overlapping regions
  - **Gradient Routing**: Max pooling routes gradients to winning neurons only
- **Benefits**:
  - Reduces computational load for subsequent layers
  - Provides translation invariance
  - Controls overfitting through dimensionality reduction

#### `FCLayer.java` (Fully Connected Layer)
- **Purpose**: Dense layer connecting all inputs to all outputs
- **Features**:
  - **Tensor Flattening**: Automatically converts 3D tensors to 1D for processing
  - **Weight Matrix**: Full connectivity between input and output neurons
  - **Activation Integration**: Applies specified activation function to outputs
- **Use Cases**:
  - Final classification layer in CNNs
  - Hidden layers in traditional neural networks
  - Feature combination after convolutional feature extraction

### Loss Functions (`src/main/java/com/portfolio/nn/network/loss/`)

#### `LossFunction.java`
- **Purpose**: Interface for loss function implementations
- **Methods**:
  - `calculateLoss(double[] predicted, double[] actual)` - Compute loss value
  - `calculateGradient(double[] predicted, double[] actual)` - Compute gradients for backpropagation

#### `CategoricalCrossEntropy.java`
- **Purpose**: Multi-class classification loss function
- **Mathematical Formula**: `L = -Σ(y_true * log(y_pred))`
- **Features**:
  - **Numerical Stability**: Epsilon clamping prevents log(0) errors
  - **Gradient Computation**: Efficient derivative calculation for backpropagation
  - **Multi-class Support**: Handles any number of output classes
- **Use Cases**:
  - MNIST digit classification (10 classes)
  - Image classification tasks
  - Any multi-class classification problem

### Data Management (`src/main/java/com/portfolio/nn/data/`)

#### `MNISTLoader.java`
- **Purpose**: MNIST dataset loader
- **Functionality**:
  - Loads MNIST training/test images and labels
  - Handles binary IDX file format
  - Normalizes pixel values (0-1 range)

#### `DataLoader.java`
- **Purpose**: Abstract data loading interface
- **Inner Class**: `Dataset` - Container for images and labels

### Models (`src/main/java/com/portfolio/nn/model/`)

#### `Model.java` / `modelModel.java`
- **Purpose**: JPA entity for model persistence
- **Fields**: Model metadata (name, type, architecture)

#### `TrainingSession.java`
- **Purpose**: Tracks training progress and metrics
- **Fields**: Current epoch, batch, accuracy, session status

### Services (`src/main/java/com/portfolio/nn/service/`)

#### `ModelService.java`
- **Purpose**: Business logic for model operations
- **Functionality**: Model CRUD operations, validation

#### `TrainingService.java`
- **Purpose**: Training session management
- **Functionality**: Start/stop training, progress tracking

### Utilities (`src/main/java/com/portfolio/nn/util/`)

#### `DataUtils.java`
- **Purpose**: Data processing utilities
- **Methods**:
  - `oneHotEncode()` - Convert labels to one-hot vectors
  - `getMaxIndex()` - Find maximum value index (for predictions)

### Resources (`src/main/resources/`)

#### `application.properties`
- **Purpose**: Spring Boot configuration
- **Settings**: Database, server port, logging configuration

#### `archive/`
- **Purpose**: MNIST dataset files
- **Files**:
  - `train-images.idx3-ubyte` - Training images (60,000)
  - `train-labels.idx1-ubyte` - Training labels
  - `t10k-images.idx3-ubyte` - Test images (10,000)
  - `t10k-labels.idx1-ubyte` - Test labels

#### `savedModels/`
- **Purpose**: Directory for saved model files
- **Subdirectories**: `ffn/`, `cnn/`, `rnn/` (for different model types)

---

## Frontend (React TypeScript) - `/frontend_react/ml-front/`

### Core Application Files

#### `src/App.tsx`
- **Purpose**: Root React component
- **Structure**: Header + Dashboard layout
- **Styling**: Tailwind CSS with custom background

#### `src/main.tsx`
- **Purpose**: React application entry point
- **Functionality**: Renders App component to DOM

#### `package.json`
- **Purpose**: Node.js project configuration
- **Dependencies**:
  - React 19 with TypeScript
  - Tailwind CSS for styling
  - Vite for build tooling

### Components (`src/components/`)

#### `Header.tsx`
- **Purpose**: Application header/navigation
- **Features**: Branding, navigation links

#### `StatsCard.tsx`
- **Purpose**: Reusable statistics display component
- **Usage**: Dashboard metrics visualization

#### `modelForm.tsx`
- **Purpose**: Form component for model creation/editing
- **Fields**: Model name, architecture parameters

### Dashboard (`src/dashboard/`)

#### `dashboard.tsx`
- **Purpose**: Main dashboard interface
- **Features**:
  - Model management interface
  - Training controls
  - Results visualization
  - Statistics display

### Services (`src/services/`)

#### `api.ts`
- **Purpose**: API client for backend communication
- **Features**:
  - HTTP request wrapper with error handling
  - Timeout configuration (10s default)
  - Typed API methods for all endpoints
- **API Modules**:
  - `testApi` - Connection testing
  - `dashboardApi` - Dashboard statistics
  - `modelApi` - Model CRUD operations
  - `trainingApi` - Training management
  - `resultsApi` - Results retrieval

#### `constants.ts`
- **Purpose**: TypeScript type definitions
- **Types**: `Model`, `Training`, `Result` interfaces

### Assets (`src/assets/`)
- **Purpose**: Static assets (SVG icons, images)
- **Files**: Various UI icons and graphics

### Configuration Files

#### `vite.config.ts`
- **Purpose**: Vite build tool configuration
- **Settings**: React plugin, dev server settings

#### `tsconfig.json`
- **Purpose**: TypeScript compiler configuration
- **Settings**: Strict type checking, module resolution

#### `tailwind.config.js`
- **Purpose**: Tailwind CSS configuration
- **Settings**: Custom colors, responsive breakpoints

---

## Neural Network Architectures

### Feed-Forward Networks (FFN)
- **Architecture**: Input → Hidden Layers → Output
- **Layer Types**: Fully connected (dense) layers only
- **Data Flow**: 1D vectors through matrix multiplications
- **Best For**: Tabular data, simple classification, regression
- **MNIST Setup**: 784 inputs → [128, 64] hidden → 10 outputs

### Convolutional Networks (CNN)
- **Architecture**: Conv → Pool → Conv → Pool → FC → Output
- **Layer Types**: Convolutional, Pooling, Fully Connected
- **Data Flow**: 3D tensors preserving spatial relationships
- **Best For**: Image classification, computer vision, spatial data
- **MNIST Setup**: 28×28×1 → Conv(32 filters) → Pool → Conv(64 filters) → Pool → FC(128) → 10 outputs

### Layer Composition Examples

#### Basic CNN for MNIST:
```java
ConvolutionalNetwork cnn = new ConvolutionalNetwork()
    .addLayer(new ConvolutionalLayer(32, 3, 1, 1, ActivationFunction.RELU))
    .addLayer(new PoolingLayer(2, 2, PoolingType.MAX))
    .addLayer(new ConvolutionalLayer(64, 3, 1, 1, ActivationFunction.RELU))
    .addLayer(new PoolingLayer(2, 2, PoolingType.MAX))
    .addLayer(new FCLayer(128, ActivationFunction.RELU))
    .addLayer(new FCLayer(10, ActivationFunction.SIGMOID));
```

#### Advanced CNN with Multiple Convolutions:
```java
ConvolutionalNetwork advancedCNN = new ConvolutionalNetwork()
    .addLayer(new ConvolutionalLayer(16, 5, 1, 2, ActivationFunction.RELU))
    .addLayer(new ConvolutionalLayer(32, 3, 1, 1, ActivationFunction.RELU))
    .addLayer(new PoolingLayer(2, 2, PoolingType.MAX))
    .addLayer(new ConvolutionalLayer(64, 3, 1, 1, ActivationFunction.RELU))
    .addLayer(new ConvolutionalLayer(64, 3, 1, 1, ActivationFunction.RELU))
    .addLayer(new PoolingLayer(2, 2, PoolingType.AVERAGE))
    .addLayer(new FCLayer(256, ActivationFunction.RELU))
    .addLayer(new FCLayer(128, ActivationFunction.RELU))
    .addLayer(new FCLayer(10, ActivationFunction.SIGMOID));
```

## Data Flow Architecture

### Model Creation Flow
1. **Frontend**: User selects model type (FFN/CNN) and fills architecture form (`modelForm.tsx`)
2. **API**: POST request via `modelApi.create()` with model specifications (`api.ts`)
3. **Backend**: `ModelController.postModels()` validates and processes request
4. **Service**: `ModelService` handles business logic and architecture validation
5. **Database**: Model metadata saved via JPA repository
6. **Network**: Appropriate network type instantiated based on model type

### Training Flow
1. **Frontend**: User initiates training with hyperparameters (`dashboard.tsx`)
2. **API**: POST to `/training/start` via `trainingApi.start()` with training config
3. **Backend**: `TrainingController` creates `TrainingSession` and selects network type
4. **Service**: `TrainingService` manages training process and progress tracking
5. **Network**: Either `FeedForwardNetwork.train()` or `ConvolutionalNetwork.train()` executes
6. **Progress**: Real-time updates via status endpoint with loss/accuracy metrics

### CNN Data Processing Flow
1. **Loading**: `MNISTLoader` reads binary dataset files into 3D tensors
2. **Preprocessing**: Images normalized to [0,1], labels one-hot encoded
3. **Forward Pass**: 
   - Convolutional layers extract spatial features
   - Pooling layers reduce dimensionality
   - FC layers perform final classification
4. **Backward Pass**:
   - Loss gradients computed via `CategoricalCrossEntropy`
   - Gradients backpropagated through each layer
   - Weights updated via gradient descent
5. **Evaluation**: Accuracy calculated on test set with confusion matrix

### FFN Data Processing Flow
1. **Loading**: MNIST images flattened to 784-dimensional vectors
2. **Preprocessing**: Pixel normalization and label encoding
3. **Training**: Traditional backpropagation through dense layers
4. **Evaluation**: Classification accuracy on test set

## Key Design Patterns

### Backend Patterns
- **MVC Architecture**: Clear separation of Controllers, Services, and Models
- **Repository Pattern**: JPA repositories for data persistence
- **Strategy Pattern**: Pluggable activation functions and loss functions
- **Chain of Responsibility**: Linked-list layer architecture in CNNs
- **Template Method**: Abstract `LayerBase` with concrete implementations
- **Factory Pattern**: Network type selection based on model configuration
- **Observer Pattern**: Training progress monitoring and real-time updates

### Frontend Patterns
- **Component Composition**: Reusable UI components with props interface
- **Service Layer**: Centralized API communication with error handling
- **Type Safety**: Full TypeScript integration with interface definitions
- **Responsive Design**: Tailwind CSS utilities for mobile-first design
- **State Management**: React hooks for component state and side effects

## File Extensions and Formats

### Model Files (`.fnn`)
- **Format**: JSON serialization of complete network architecture
- **Location**: `savedModels/` directory with subdirectories for model types
- **FFN Structure**: Includes layer dimensions, weights matrices, biases, activation functions
- **CNN Structure**: Includes layer sequence, filter weights, pooling parameters, FC layer weights
- **Metadata**: Model type, creation date, training history, hyperparameters

### Dataset Files (`.idx3-ubyte`, `.idx1-ubyte`)
- **Format**: MNIST binary format
- **Purpose**: Efficient storage of image/label data
- **Processing**: Custom loader handles binary parsing

## Development Workflow

### Backend Development
1. **Build**: `./gradlew build`
2. **Run**: `./gradlew bootRun`
3. **Test**: `./gradlew test`
4. **Hot Reload**: Spring Boot DevTools enabled

### Frontend Development
1. **Install**: `npm install`
2. **Dev Server**: `npm run dev`
3. **Build**: `npm run build`
4. **Lint**: `npm run lint`

## API Integration

The frontend communicates with the backend through a well-defined REST API. All requests include proper error handling, timeouts, and type safety through TypeScript interfaces.

### Error Handling
- **Frontend**: Centralized error handling in `api.ts`
- **Backend**: Spring Boot exception handling
- **User Feedback**: Error messages displayed in UI

### CORS Configuration
- **Backend**: Configured for `http://localhost:5173`
- **Development**: Allows frontend-backend communication
- **Production**: Should be updated for production domains

This architecture provides a scalable, maintainable foundation for neural network experimentation and visualization.


## Performance Considerations

### Memory Usage
- **CNN Models**: Higher memory usage due to 3D tensor operations and filter storage
- **Parallel Processing**: ConvolutionalLayer uses Java Streams for multi-threaded operations
- **Memory Optimization**: Layers cache only necessary tensors for backpropagation

### Computational Complexity
- **FFN**: O(n×m) per layer where n=inputs, m=outputs
- **CNN**: O(F×K²×H×W) per conv layer where F=filters, K=kernel size, H×W=output dimensions
- **Pooling**: O(H×W×D) where D=depth, significantly faster than convolution

### Scalability
- **Batch Processing**: Both network types support mini-batch training
- **Layer Modularity**: Easy to add new layer types or modify existing ones
- **Thread Safety**: Synchronized operations in multi-threaded environments

## Known Issues & Limitations

### Current Limitations
- **Dataset Support**: Currently optimized for MNIST (28×28 grayscale images)
- **Memory Consumption**: Large CNN models may require significant RAM during training
- **Session Management**: sessionId and modelId are currently the same identifier
- **GPU Acceleration**: No CUDA support - CPU-only implementation

### Future Enhancements
- **Additional Layer Types**: Batch normalization, dropout, LSTM layers
- **Optimization Algorithms**: Adam, RMSprop, momentum-based optimizers
- **Data Augmentation**: Image rotation, scaling, noise injection
- **Model Visualization**: Network architecture diagrams and feature map visualization
- **Distributed Training**: Multi-GPU and distributed computing support 