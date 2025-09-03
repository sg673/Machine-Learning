# Neural Network Framework

A Java Spring Boot framework supporting multiple neural network architectures for diverse datasets.

## Project Structure

```
src/main/java/com/portfolio/nn/
├── network/                    # Neural network implementations
│   ├── activation/            # Activation functions
│   ├── layers/               # Layer types (future)
│   ├── loss/                 # Loss functions (future)
│   ├── NeuralNetworkBase.java # Base interface
│   └── FeedForwardNetwork.java # FFN implementation
├── data/                      # Dataset loaders
│   ├── DataLoader.java       # Base interface
│   └── MNISTLoader.java      # MNIST implementation
└── util/                     # Utility classes
    └── DataUtils.java        # Data processing utilities
```

## Supported Networks
- Feed Forward Networks (FFN)
- Convolutional Neural Networks (CNN) - *planned*
- Recurrent Neural Networks (RNN) - *planned*

## Supported Datasets
- MNIST digit classification
- CIFAR-10 - *planned*
- Custom CSV datasets - *planned*

## Usage

```java
// Create network
FeedForwardNetwork network = new FeedForwardNetwork(
    ActivationFunction.RELU, 784, 128, 10
);

// Load data
DataLoader loader = new MNISTLoader();
DataLoader.Dataset trainData = loader.loadTraining();

// Train
double[][] labels = DataUtils.oneHotEncode(trainData.getLabels());
network.train(trainData.getImages(), labels, 0.01, 10);

// Evaluate
double accuracy = network.evaluate(testData.getImages(), testLabels);
```

## Running

```bash
./gradlew bootRun
```