package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PsiContainsChecker {

    /**
     * Checks if the Psi tree from base contains a PsiElement that is structurally equivalent to the originalElement.
     * @param base The root of the tree from where to search using this checker instance.
     * @param originalElement The element to search for
     * @return The equivalent element if found, null otherwise.
     */
    @Nullable
    public PsiElement findEquivalent(final PsiElement base, final PsiElement originalElement) {
        if (!base.isValid()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Collection<PsiElement> allIndividualElements = PsiTreeUtil.collectElementsOfType(base, PsiElement.class);
        for (PsiElement individualElement : allIndividualElements) {
            String elementText = individualElement.getText();
            if (elementText.equals(originalElement.getText())) {
                return individualElement;
            }
        }
        return null;
    }
}
