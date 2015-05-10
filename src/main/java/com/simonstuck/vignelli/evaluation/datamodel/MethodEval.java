package com.simonstuck.vignelli.evaluation.datamodel;

import org.jetbrains.annotations.NotNull;

public class MethodEval {
    private final String name;
    private final MethodMetrics metrics;
    private final MethodClassification classification;

    public MethodEval(String name, @NotNull MethodMetrics metrics, @NotNull MethodClassification classification) {
        this.name = name;
        this.metrics = metrics;
        this.classification = classification;
    }

    public String getName() {
        return name;
    }

    public MethodMetrics getMetrics() {
        return metrics;
    }

    public MethodClassification getClassification() {
        return classification;
    }
}