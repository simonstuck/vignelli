package com.simonstuck.vignelli.inspection.identification.impl;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;

import org.jetbrains.annotations.NotNull;

public class TrainWreckIdentification implements ProblemDescriptorProvider {

    private static final String SHORT_DESCRIPTION = "This piece of code violates the Law of Demeter";

    @NotNull
    private final PsiMethodCallExpression finalCall;

    public TrainWreckIdentification(@NotNull PsiMethodCallExpression finalCall) {
        this.finalCall = finalCall;
    }

    @Override
    public ProblemDescriptor problemDescriptor(InspectionManager manager) {
        return manager.createProblemDescriptor(finalCall, finalCall,SHORT_DESCRIPTION,ProblemHighlightType.GENERIC_ERROR_OR_WARNING,false);
    }

    @NotNull
    public PsiMethodCallExpression getFinalCall() {
        return finalCall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TrainWreckIdentification that = (TrainWreckIdentification) o;

        return finalCall.equals(that.finalCall);

    }

    @Override
    public int hashCode() {
        return finalCall.hashCode();
    }

    public PsiMethodCallExpression criticalCall() {

        return null;
    }
}
