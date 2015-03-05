package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.ProblemDescriptor;

public class ProblemIdentificationBuilder {
    private ProblemDescriptor problemDescriptor;
    private String name;
    private String shortDescription;
    private String longDescription;

    public ProblemIdentificationBuilder setProblemDescriptor(ProblemDescriptor problemDescriptor) {
        this.problemDescriptor = problemDescriptor;
        return this;
    }

    public ProblemIdentificationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProblemIdentificationBuilder setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public ProblemIdentificationBuilder setLongDescription(String longDescription) {
        this.longDescription = longDescription;
        return this;
    }

    public ProblemIdentification build() {
        return new ProblemIdentification(problemDescriptor, name);
    }
}