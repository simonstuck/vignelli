package com.simonstuck.vignelli.evaluation.datamodel;

public class MethodClassification {
    private final boolean isComplex;

    public MethodClassification(boolean isComplex) {
        this.isComplex = isComplex;
    }

    public boolean isComplex() {
        return isComplex;
    }
}
