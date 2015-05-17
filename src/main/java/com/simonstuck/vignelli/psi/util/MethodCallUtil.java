package com.simonstuck.vignelli.psi.util;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;

import org.jetbrains.annotations.Nullable;

public class MethodCallUtil {
    private MethodCallUtil() {
        throw new AssertionError();
    }

    /**
     * Calculates the type difference of a given method call chain
     * @param methodCallExpression The method call expression for which to calculate the type difference.
     * @return The type difference.
     */
    public static int calculateTypeDifference(@Nullable PsiMethodCallExpression methodCallExpression) {
        int typeDifference = -1;
        PsiType currentType = null;

        PsiExpression currentExpression = methodCallExpression;

        while (currentExpression != null) {
            PsiType newType = currentExpression.getType();
            if (newType != PsiType.VOID && (currentType != null ? !currentType.equals(newType) : newType != null)) {
                typeDifference++;
            }
            currentType = newType;

            if (currentExpression instanceof PsiMethodCallExpression) {
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) currentExpression).getMethodExpression();
                currentExpression = methodExpression.getQualifierExpression();
            } else {
                currentExpression = null;
            }
        }
        return typeDifference;
    }
}
