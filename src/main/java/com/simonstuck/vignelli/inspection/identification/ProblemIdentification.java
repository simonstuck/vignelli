package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.ProblemDescriptor;
import com.simonstuck.vignelli.utils.IOUtils;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

public class ProblemIdentification implements Identification {

    @NotNull
    private final ProblemDescriptor problemDescriptor;
    private final String name;
    private final String shortDescription;
    private final String longDescription;

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.identification.ProblemIdentification}.
     * <p>The new problem identification contains information about the problem.</p>
     * @param problemDescriptor The problem descriptor associated with the problem
     * @param name The name of the problem
     * @param shortDescription A short description of the problem
     * @param longDescription A longer description of the problem
     */
    public ProblemIdentification(@NotNull ProblemDescriptor problemDescriptor, String name,
                                 String shortDescription, String longDescription) {
        this.problemDescriptor = problemDescriptor;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    @NotNull
    public ProblemDescriptor getProblemDescriptor() {
        return problemDescriptor;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String shortDescription() {
        return shortDescription;
    }

    @Override
    public String longDescription() {
        return longDescription;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        ProblemIdentification that = (ProblemIdentification) other;
        ProblemDescriptor thatDescriptor = that.getProblemDescriptor();
        return that.name.equals(name)
                && thatDescriptor.getStartElement().equals(problemDescriptor.getStartElement())
                && thatDescriptor.getEndElement().equals(problemDescriptor.getEndElement())
                && thatDescriptor.getDescriptionTemplate().equals(problemDescriptor.getDescriptionTemplate())
                && thatDescriptor.getLineNumber() == problemDescriptor.getLineNumber()
                && thatDescriptor.isAfterEndOfLine() == problemDescriptor.isAfterEndOfLine()
                && thatDescriptor.getHighlightType() == problemDescriptor.getHighlightType()
                && thatDescriptor.getPsiElement() == problemDescriptor.getPsiElement();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
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
        return name() + ": " + problemDescriptor.getLineNumber();
    }

    public String descriptionTemplate() {
        try {
            return IOUtils.readFile(getClass().getResource("/problemDescriptionTemplates/trainWreckDescription.html").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }
}
