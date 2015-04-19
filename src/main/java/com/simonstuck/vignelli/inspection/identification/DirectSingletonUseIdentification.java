package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiMethodCallExpression;

public class DirectSingletonUseIdentification {
    private static final String SHORT_DESCRIPTION = "Direct Use of Singleton";
    private final PsiMethodCallExpression methodCall;

    public DirectSingletonUseIdentification(PsiMethodCallExpression methodCall) {
        this.methodCall = methodCall;
    }

    public ProblemDescriptor problemDescriptor(InspectionManager manager) {
        return manager.createProblemDescriptor(methodCall, methodCall, SHORT_DESCRIPTION, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
    }

}
