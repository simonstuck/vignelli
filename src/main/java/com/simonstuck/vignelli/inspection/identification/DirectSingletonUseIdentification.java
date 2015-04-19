package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.PsiMethodCallExpression;

public class DirectSingletonUseIdentification {
    private final PsiMethodCallExpression methodCall;

    public DirectSingletonUseIdentification(PsiMethodCallExpression methodCall) {
        this.methodCall = methodCall;
    }
}
