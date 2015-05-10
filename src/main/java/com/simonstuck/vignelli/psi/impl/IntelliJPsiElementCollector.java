package com.simonstuck.vignelli.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.psi.PsiElementCollector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class IntelliJPsiElementCollector implements PsiElementCollector {
    @NotNull
    @Override
    public <T extends PsiElement> Collection<T> collectElementsOfType(@Nullable PsiElement baseElement, @NotNull Class<T>... classes) {
        return PsiTreeUtil.collectElementsOfType(baseElement, classes);
    }
}
