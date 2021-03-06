package com.simonstuck.vignelli.evaluation.datamodel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassSingletonUseClassification {
    @NotNull
    private final String name;
    @NotNull
    private final SingletonClassClassification singletonClassClassification;

    private final Map<String, Set<SingletonMethodCallPrediction>> methodCallPredictions = new HashMap<String, Set<SingletonMethodCallPrediction>>();

    public ClassSingletonUseClassification(@NotNull String name, @NotNull SingletonClassClassification singletonClassClassification) {
        this.name = name;
        this.singletonClassClassification = singletonClassClassification;
    }

    public void addMethodCallPrediction(String methodName, @NotNull SingletonMethodCallPrediction prediction) {
        Set<SingletonMethodCallPrediction> existingPredictions = methodCallPredictions.get(methodName);
        if (existingPredictions == null) {
            existingPredictions = new HashSet<SingletonMethodCallPrediction>();
        }
        existingPredictions.add(prediction);
        methodCallPredictions.put(methodName, existingPredictions);
    }

    public String getName() {
        return name;
    }
}
