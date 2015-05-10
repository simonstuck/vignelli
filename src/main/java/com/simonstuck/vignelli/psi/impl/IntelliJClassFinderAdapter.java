package com.simonstuck.vignelli.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.simonstuck.vignelli.psi.ClassFinder;

public class IntelliJClassFinderAdapter implements ClassFinder {
    
    private final JavaPsiFacade javaPsiFacade;

    /**
     * Creates a default IntelliJ class finder, a wrapper around JavaPsiFacade
     * @param project The project for which to create the facade.
     */
    public IntelliJClassFinderAdapter(Project project) {
        javaPsiFacade = JavaPsiFacade.getInstance(project);
    }

    @Override
    public PsiClass findClass(GlobalSearchScope searchScope, String qualifiedName) {
        return javaPsiFacade.findClass(qualifiedName, searchScope);
    }
}
