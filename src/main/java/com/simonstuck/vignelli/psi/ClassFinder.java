package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * A {@link com.simonstuck.vignelli.psi.ClassFinder} instance allows clients to search for a
 * given class name within some given scope.
 */
public interface ClassFinder {

    /**
     * Search the given search scope for the class given by the qualifiedName.
     * @param searchScope The scope to search.
     * @param qualifiedName The qualified name of the class to look for
     * @return The class iff it is found, null otherwise.
     */
    PsiClass findClass(GlobalSearchScope searchScope, String qualifiedName);
}
