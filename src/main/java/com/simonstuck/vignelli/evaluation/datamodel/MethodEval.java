package com.simonstuck.vignelli.evaluation.datamodel;

public class MethodEval {
    private final String name;
    private final int linesOfCode;
    private final int cyclomaticComplexity;
    private final int numParameters;
    private final int nestedBlockDepth;
    private final int linesOfComments;
    private final boolean isComplex;

    public MethodEval(String name, int linesOfCode, int cyclomaticComplexity, int numParameters, int nestedBlockDepth, int linesOfComments, boolean isComplex) {
        this.name = name;
        this.linesOfCode = linesOfCode;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.numParameters = numParameters;
        this.nestedBlockDepth = nestedBlockDepth;
        this.linesOfComments = linesOfComments;
        this.isComplex = isComplex;
    }

    public String getName() {
        return name;
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

    public boolean isComplex() {
        return isComplex;
    }
}
