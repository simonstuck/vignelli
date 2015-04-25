package com.simonstuck.vignelli.psi;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;

import org.jetbrains.annotations.Nullable;

public class PsiContainsChecker extends PsiRecursiveElementVisitor {

    private final PsiElement base;
    private PsiElement equivalent;
    private PsiElement originalElement;

    /**
     * Creates a new {@link com.simonstuck.vignelli.psi.PsiContainsChecker} that can check the tree from the given base.
     * @param base The root of the tree from where to search using this checker instance.
     */
    public PsiContainsChecker(PsiElement base) {
        this.base = base;
    }

    /**
     * Checks if the Psi tree from base contains a PsiElement that is structurally equivalent to the originalElement.
     * @param originalElement The element to search for
     * @return The equivalent element if found, null otherwise.
     */
    @Nullable
    public PsiElement findEquivalent(PsiElement originalElement) {
        equivalent = null;
        this.originalElement = originalElement;
        base.accept(this);

        return equivalent;
    }

    @Override
    public void visitElement(PsiElement element) {
        super.visitElement(element);
        if (PsiEquivalenceUtil.areElementsEquivalent(element, originalElement)) {
            equivalent = element;
        }
    }
}
