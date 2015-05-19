package com.simonstuck.vignelli.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;

public class PsiElementUtil {
    /**
     * Checks if any of the given {@link com.intellij.psi.PsiElement}s are null or invalid.
     *
     * @param elements The elements to check.
     * @return True iff any of the elements are null or invalid.
     */
    public static boolean isAnyNullOrInvalid(PsiElement... elements) {
        for (PsiElement element : elements) {
            if (element == null || !element.isValid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the given {@link com.intellij.psi.PsiType}s are null or invalid.
     *
     * @param types The types to check.
     * @return True iff any of the types are null or invalid.
     */
    public static boolean isAnyTypeNullOrInvalid(PsiType... types) {
        for (PsiType type : types) {
            if (type == null || !type.isValid()) {
                return true;
            }
        }
        return false;
    }
}