package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.ProblemDescriptor;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

public class ProblemIdentification implements Identification {

    @NotNull
    private final ProblemDescriptor problemDescriptor;

    private ProblemIdentification(@NotNull ProblemDescriptor problemDescriptor) {
        this.problemDescriptor = problemDescriptor;
    }

    public static ProblemIdentification createWithProblemDescriptor(@NotNull ProblemDescriptor problemDescriptor) {
        return new ProblemIdentification(problemDescriptor);
    }

    @NotNull
    public ProblemDescriptor getProblemDescriptor() {
        return problemDescriptor;
    }

    @Override
    public String getName() {
        return problemDescriptor.toString();
    }

    @Override
    public String getShortDescription() {
        return "";
    }

    @Override
    public String getLongDescription() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProblemIdentification that = (ProblemIdentification) o;
        ProblemDescriptor thatDescriptor = that.getProblemDescriptor();
        return thatDescriptor.getStartElement().equals(problemDescriptor.getStartElement()) &&
                thatDescriptor.getEndElement().equals(problemDescriptor.getEndElement()) &&
                thatDescriptor.getDescriptionTemplate().equals(problemDescriptor.getDescriptionTemplate()) &&
                thatDescriptor.getLineNumber() == problemDescriptor.getLineNumber() &&
                thatDescriptor.isAfterEndOfLine() == problemDescriptor.isAfterEndOfLine() &&
                thatDescriptor.getHighlightType() == problemDescriptor.getHighlightType() &&
                thatDescriptor.getPsiElement() == problemDescriptor.getPsiElement();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(problemDescriptor.getStartElement())
                .append(problemDescriptor.getEndElement())
                .append(problemDescriptor.getDescriptionTemplate())
                .append(problemDescriptor.getLineNumber())
                .append(problemDescriptor.isAfterEndOfLine())
                .append(problemDescriptor.getHighlightType())
                .append(problemDescriptor.getPsiElement()).toHashCode();
    }

    @Override
    public String toString() {
        return getName() + ": " + getShortDescription();
    }
}
