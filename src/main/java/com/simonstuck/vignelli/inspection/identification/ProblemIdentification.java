package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.TrainWreckVariableImprovementOpportunity;
import com.simonstuck.vignelli.utils.IOUtils;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

public class ProblemIdentification {

    @NotNull
    private final ProblemDescriptor problemDescriptor;
    private final String name;
    private final PsiElement element;
    private VirtualFile virtualFile;

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.identification.ProblemIdentification}.
     * <p>The new problem identification contains information about the problem.</p>
     * @param problemDescriptor The problem descriptor associated with the problem
     * @param name The name of the problem
     */
    public ProblemIdentification(@NotNull ProblemDescriptor problemDescriptor, String name) {
        this.problemDescriptor = problemDescriptor;
        this.name = name;
        this.element = problemDescriptor.getPsiElement();
        this.virtualFile = element.getContainingFile().getVirtualFile();
    }

    @NotNull
    public ProblemDescriptor getProblemDescriptor() {
        return problemDescriptor;
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
                && Objects.equals(thatDescriptor.getStartElement(), problemDescriptor.getStartElement())
                && Objects.equals(thatDescriptor.getStartElement(), problemDescriptor.getStartElement())
                && Objects.equals(thatDescriptor.getEndElement(), problemDescriptor.getEndElement())
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
        return "ProblemIdentification: " + problemDescriptor.getLineNumber();
    }

    public String descriptionTemplate() {
        try {
            return IOUtils.readFile(getClass().getResource("/problemDescriptionTemplates/trainWreckDescription.html").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public VirtualFile virtualFile() {
        return virtualFile;
    }

    public Optional<TrainWreckVariableImprovementOpportunity> improvementOpportunity() {
        PsiVariable parent = PsiTreeUtil.getParentOfType(element, PsiVariable.class);
        if (parent != null) {
            return Optional.of(new TrainWreckVariableImprovementOpportunity(element,parent));
        } else {
            return Optional.empty();
        }
    }
}
