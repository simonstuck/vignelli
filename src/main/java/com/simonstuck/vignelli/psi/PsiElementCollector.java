package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiElementCollector {
    /**
     * Collects all elements in the tree below the baseElement of the given type.
     * @param baseElement The base element from which to search.
     * @param classes The classes to search for
     * @return A collection of all matches.
     */
    @NotNull
    <T extends PsiElement> Collection<T> collectElementsOfType(@Nullable PsiElement baseElement, @NotNull final Class<T>... classes);
}
