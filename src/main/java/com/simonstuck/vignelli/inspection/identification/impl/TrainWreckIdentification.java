package com.simonstuck.vignelli.inspection.identification.impl;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.psi.util.MethodCallUtil;

import org.jetbrains.annotations.NotNull;

public class TrainWreckIdentification implements ProblemDescriptorProvider {

    private static final String SHORT_DESCRIPTION = "This piece of code violates the Law of Demeter";
    public static final int TRAIN_WRECK_TYPE_DIFFERENCE_THRESHOLD = 1;

    @NotNull
    private final PsiMethodCallExpression finalCall;

    private final PsiMethodCallExpression criticalCall;

    public TrainWreckIdentification(@NotNull PsiMethodCallExpression finalCall) {
        this.finalCall = finalCall;
        criticalCall = getCriticalCall(finalCall);
    }

    private PsiMethodCallExpression getCriticalCall(PsiMethodCallExpression finalCall) {
        PsiMethodCallExpression criticalCall = finalCall;

        PsiExpression currentExpression = finalCall;
        while (currentExpression != null) {

            if (currentExpression instanceof PsiMethodCallExpression) {

                final PsiMethodCallExpression currentMethodCall = (PsiMethodCallExpression) currentExpression;
                int newTypeDifference = MethodCallUtil.calculateTypeDifference(currentMethodCall);
                if (newTypeDifference >= TRAIN_WRECK_TYPE_DIFFERENCE_THRESHOLD) {
                    criticalCall = currentMethodCall;
                }

                PsiReferenceExpression methodExpression = currentMethodCall.getMethodExpression();
                currentExpression = methodExpression.getQualifierExpression();
            } else {
                currentExpression = null;
            }
        }

        return criticalCall;
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
        return criticalCall;
    }
}
