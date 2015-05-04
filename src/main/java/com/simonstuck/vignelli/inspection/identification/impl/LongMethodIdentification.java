package com.simonstuck.vignelli.inspection.identification.impl;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;

import org.jetbrains.annotations.NotNull;

public class LongMethodIdentification implements ProblemDescriptorProvider {
    public static final String SHORT_DESCRIPTION = "Long method";

    @NotNull
    private final PsiMethod method;

    public LongMethodIdentification(@NotNull PsiMethod method) {
        this.method = method;
    }

    @Override
    public ProblemDescriptor problemDescriptor(InspectionManager manager) {
        PsiIdentifier nameIdentifier = method.getNameIdentifier();
        if (nameIdentifier != null) {
            return manager.createProblemDescriptor(nameIdentifier, nameIdentifier, SHORT_DESCRIPTION, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
        } else {
            return manager.createProblemDescriptor(method, method, SHORT_DESCRIPTION, ProblemHighlightType.GENERIC_ERROR_OR_WARNING,false);
        }
    }
}
