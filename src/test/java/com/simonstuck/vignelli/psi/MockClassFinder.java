package com.simonstuck.vignelli.psi;

import static org.mockito.Mockito.mock;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

public class MockClassFinder implements ClassFinder {
    @Override
    public PsiClass findClass(GlobalSearchScope searchScope, String qualifiedName) {
        return mock(PsiClass.class);
    }
}
