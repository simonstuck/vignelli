package com.simonstuck.vignelli.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

public class IntelliJClassFinder implements ClassFinder {
    private final JavaPsiFacade javaPsiFacade;

    public IntelliJClassFinder(Project project) {
        javaPsiFacade = JavaPsiFacade.getInstance(project);
    }

    @Override
    public PsiClass findClass(GlobalSearchScope searchScope, String qualifiedName) {
        return javaPsiFacade.findClass(qualifiedName, searchScope);
    }
}
