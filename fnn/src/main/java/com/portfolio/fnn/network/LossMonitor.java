package com.portfolio.fnn.network;

public class LossMonitor {
    private double bestLoss = Double.MAX_VALUE;
    private int stagnantEpochs = 0;
    private final double tolerance;
    private final int patience;
    private final long startTime;

    public LossMonitor(double tolerance, int patience) {
        this.tolerance = tolerance;
        this.patience = patience;
        this.startTime = System.currentTimeMillis();
    }

    public boolean shouldStop(double currentLoss, int epoch) {
        if (Math.abs(bestLoss - currentLoss) < tolerance) {
            stagnantEpochs++;
        } else {
            stagnantEpochs = 0;
            bestLoss = currentLoss;
        }
        return stagnantEpochs >= patience;
    }

    public void printStats(int epoch, double loss) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.printf("Epoch %d | Loss: %.10f | Best: %.10f | Time elapsed: %d ms%n", epoch, loss, bestLoss,
                elapsedTime);
    }

    public String getETA(int currentEpoch, int totalEpochs) {
        if (currentEpoch == 0) {
            return "ETA: N/A";
        }
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = (elapsed * (totalEpochs - currentEpoch)) / currentEpoch;
        return String.format("ETA: %dm %ds", remaining / 60000, (remaining % 60000) / 1000);
    }

}
