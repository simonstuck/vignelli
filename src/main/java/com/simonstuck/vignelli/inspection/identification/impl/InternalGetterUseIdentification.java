package com.simonstuck.vignelli.inspection.identification.impl;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;

import org.jetbrains.annotations.NotNull;

public class InternalGetterUseIdentification implements ProblemDescriptorProvider {
    public static final String SHORT_DESCRIPTION = "Internal Use of a Getter";

    @NotNull
    private final PsiMethodCallExpression methodCallExpression;

    public InternalGetterUseIdentification(@NotNull PsiMethodCallExpression methodCallExpression) {
        this.methodCallExpression = methodCallExpression;
    }


    @Override
    public ProblemDescriptor problemDescriptor(InspectionManager manager) {
        return manager.createProblemDescriptor(methodCallExpression, methodCallExpression, SHORT_DESCRIPTION, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
    }
}
