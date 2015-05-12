package com.simonstuck.vignelli.evaluation.datamodel;

public class SingletonMethodCallPrediction {
    private final boolean isPredictedSingleton;

    public SingletonMethodCallPrediction(boolean assumesSingletonCall) {
        this.isPredictedSingleton = assumesSingletonCall;
    }
}
