package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

public interface ClassFinder {
    PsiClass findClass(GlobalSearchScope searchScope, String qualifiedName);
}
