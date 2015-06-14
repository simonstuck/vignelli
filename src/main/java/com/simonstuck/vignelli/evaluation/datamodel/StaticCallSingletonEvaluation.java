package com.simonstuck.vignelli.evaluation.datamodel;

public class StaticCallSingletonEvaluation {
    private String methodCall;
    private boolean vignelliClassification;
    private boolean manualClassification;

    public StaticCallSingletonEvaluation(String methodCall, boolean vignelliClassification, boolean manualClassification) {
        this.methodCall = methodCall;
        this.vignelliClassification = vignelliClassification;
        this.manualClassification = manualClassification;
    }
}
