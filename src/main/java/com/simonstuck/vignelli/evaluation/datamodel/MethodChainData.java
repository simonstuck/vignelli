package com.simonstuck.vignelli.evaluation.datamodel;

public class MethodChainData {
    private final String className;
    private final String methodName;
    private final String methodChainText;
    private final boolean isTrainWreck;

    public MethodChainData(String className, String methodName, String methodChainText, boolean isTrainWreck) {
        this.className = className;
        this.methodName = methodName;
        this.methodChainText = methodChainText;
        this.isTrainWreck = isTrainWreck;
    }
}
