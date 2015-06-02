package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTypesUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Searcher for applicable interfaces instead of the given base class in the given search scope.</p>
 * <p>
 *     This searcher finds all interfaces and base classes of the given base class that can be
 *     used in the given search scope. In other words, all of methods used in the given search
 *     scope are defined in all of the interfaces and classes returned by this search.
 * </p>
 */
public class ApplicableInterfacesForUsedMethodsInClassSearch {

    @NotNull
    private final PsiClass baseClass;
    @NotNull
    private final SearchScope usageSearchScope;

    /**
     * Creates a new search with the given base class to attempt to replace and the search scope where it has to be replaced.
     * @param baseClass The class to replace.
     * @param usageSearchScope The scope to search for used methods.
     */
    public ApplicableInterfacesForUsedMethodsInClassSearch(@NotNull PsiClass baseClass, @NotNull SearchScope usageSearchScope) {
        this.baseClass = baseClass;
        this.usageSearchScope = usageSearchScope;
    }

    /**
     * Invokes the search.
     * @return A new set of all classes that could be used in place of the base class
     */
    public Set<PsiClass> invoke() {
        Set<PsiClass> applicableInterfaces = new HashSet<PsiClass>();
        Set<PsiClass> extendsOrImplements = getAllExtendedClassesAndImplementedInterfaces();
        Set<PsiMethod> referencedSingletonMethods = getReferencedMethodsOfBaseClassInSearchScope();

        for (PsiClass extendedClass : extendsOrImplements) {
            if (definesAllMethods(extendedClass, referencedSingletonMethods)) {
                applicableInterfaces.add(extendedClass);
            }
        }
        return applicableInterfaces;
    }

    /**
     * Finds all of the classes and interfaces that the base class extends or implements.
     * @return A new set of all of the bases of the base class.
     */
    private Set<PsiClass> getAllExtendedClassesAndImplementedInterfaces() {
        Set<PsiClass> extendsOrImplements = new HashSet<PsiClass>();
        extendsOrImplements.addAll(Arrays.asList(baseClass.getInterfaces()));
        final PsiReferenceList extendsList = baseClass.getExtendsList();
        if (extendsList != null) {
            for (PsiClassType type : extendsList.getReferencedTypes()) {
                PsiClass extendedClazz = PsiTypesUtil.getPsiClass(type);
                extendsOrImplements.add(extendedClazz);
            }
        }
        return extendsOrImplements;
    }

    /**
     * Finds all of the methods of the base class that are referenced in the search scope.
     * @return A set of all the methods that are referenced.
     */
    private Set<PsiMethod> getReferencedMethodsOfBaseClassInSearchScope() {
        Set<PsiMethod> referencedSingletonMethods = new HashSet<PsiMethod>();
        for (PsiMethod method : baseClass.getAllMethods()) {
            if (!(method instanceof ClsMethodImpl) && ReferencesSearch.search(method, usageSearchScope).findFirst() != null && !method.hasModifierProperty(PsiModifier.STATIC)) {
                referencedSingletonMethods.add(method);
            }
        }
        return referencedSingletonMethods;
    }

    /**
     * Checks if the given class defines all of the given methods.
     * @param clazz The class or interface to check for the method.
     * @param methods The methods that all have to be defined.
     * @return True iff the class defines all of the given methods.
     */
    private boolean definesAllMethods(PsiClass clazz, Set<PsiMethod> methods) {
        boolean allFound = true;
        for (PsiMethod requiredMethod : methods) {
            allFound &= PsiClassImplUtil.findMethodBySignature(clazz, requiredMethod, true) != null;
        }
        return allFound;
    }
}
