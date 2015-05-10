package com.simonstuck.vignelli.evaluation.datamodel;

public class MethodMetrics {
    private final int linesOfCode;
    private final int cyclomaticComplexity;
    private final int numParameters;
    private final int nestedBlockDepth;
    private final int linesOfComments;

    public MethodMetrics(int linesOfCode, int cyclomaticComplexity, int numParameters, int nestedBlockDepth, int linesOfComments) {
        this.linesOfCode = linesOfCode;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.numParameters = numParameters;
        this.nestedBlockDepth = nestedBlockDepth;
        this.linesOfComments = linesOfComments;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public int getNumParameters() {
        return numParameters;
    }

    public int getNestedBlockDepth() {
        return nestedBlockDepth;
    }

    public int getLinesOfComments() {
        return linesOfComments;
    }
}
