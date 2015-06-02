package com.simonstuck.vignelli.psi.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
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

            if (!isStaticMethodCall(currentExpression) && newType != PsiType.VOID && (currentType != null ? !currentType.equals(newType) : newType != null)) {
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

    public static boolean containsStaticCalls(@Nullable PsiMethodCallExpression methodCallExpression) {
        PsiExpression currentExpression = methodCallExpression;

        while (currentExpression != null) {
            if (isStaticMethodCall(currentExpression)) {
                return true;
            } else if (currentExpression instanceof PsiMethodCallExpression) {
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) currentExpression).getMethodExpression();
                currentExpression = methodExpression.getQualifierExpression();
            } else {
                currentExpression = null;
            }
        }
        return false;
    }

    private static boolean isPsiClassReference(PsiElement element) {
        if (element instanceof PsiReferenceExpression) {
            PsiReferenceExpression ref = (PsiReferenceExpression) element;
            return ref.resolve() instanceof PsiClass;
        }
        return false;
    }

    private static boolean isStaticMethodCall(PsiExpression currentExpression) {
        if (!currentExpression.isValid()) {
            return false;
        }
        if (currentExpression instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) currentExpression;
            PsiMethod method = methodCallExpression.resolveMethod();
            if (!PsiElementUtil.isAnyNullOrInvalid(method)) {
                assert method != null;
                return method.hasModifierProperty(PsiModifier.STATIC);
            }
        }
        return false;
    }

    /**
     * Calculates the length of a given method call chain.
     * @param finalCall The last call of the chain to be sized.
     * @return The number of elements in the method call chain.
     */
    public static int getLength(PsiMethodCallExpression finalCall) {
        if (finalCall == null) {
            return -1;
        }

        int currentLength = 0;
        PsiExpression currentExpression = finalCall;
        while (currentExpression != null) {

            currentLength++;

            if (currentExpression instanceof PsiMethodCallExpression) {
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) currentExpression).getMethodExpression();
                currentExpression = methodExpression.getQualifierExpression();
            } else {
                currentExpression = null;
            }
        }
        return currentLength;
    }

    /**
     * Retrieves the final qualifier of a method call chain.
     * @param element An element of a method call chain
     * @return The final qualifier of the given method call chain.
     */
    public static PsiExpression getFinalQualifier(PsiExpression element) {
        if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression expression = (PsiMethodCallExpression) element;
            PsiReferenceExpression methodRefExpression = expression.getMethodExpression();
            return getFinalQualifier(methodRefExpression.getQualifierExpression());
        } else {
            return element;
        }
    }
}
