package com.simonstuck.vignelli.psi.util;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;

public class NavigationUtil {

    /**
     * Attempts to navigate the editor to the given element and requests focus.
     * @param element The element to navigate to.
     */
    public static void navigateToElement(PsiElement element) {
        navigateToElement(element, true);
    }

    /**
     * Attempts to navigate the editor to the given element
     * @param element The element to navigate to.
     * @param requestFocus Whether or not the editor should request focus.
     */
    public static void navigateToElement(PsiElement element, boolean requestFocus) {
        PsiElement navigationElement = element.getNavigationElement();
        if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate()) {
            ((Navigatable) navigationElement).navigate(requestFocus);
        }
    }
}
