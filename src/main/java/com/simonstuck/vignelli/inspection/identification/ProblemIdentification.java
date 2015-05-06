package com.simonstuck.vignelli.inspection.identification;

import com.google.common.base.Optional;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.jgoodies.common.base.Objects;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.psi.util.EditorUtil;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class ProblemIdentification implements com.simonstuck.vignelli.Templatable {

    @NotNull
    protected final ProblemDescriptor problemDescriptor;
    private final String name;
    protected final PsiElement element;

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
    }

    @NotNull
    public ProblemDescriptor getProblemDescriptor() {
        return problemDescriptor;
    }

    /**
     * Returns an {@link com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity} for this problem if one exists
     * @return An optional {@link com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity}.
     */
    public Optional<? extends ImprovementOpportunity> improvementOpportunity() {
        return Optional.absent();
    }

    /**
     * Navigates the current editor to the start element associated with this problem identification.
     */
    public void navigateToElement() {
        EditorUtil.navigateToElement(problemDescriptor.getPsiElement());
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
                && Objects.equals(that.element, element)
                && Objects.equals(thatDescriptor, problemDescriptor);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(element)
                .append(problemDescriptor).toHashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public PsiElement getElement() {
        return element;
    }
}
