package com.simonstuck.vignelli.inspection.identification.impl;

import com.google.common.base.Optional;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.psi.ClassFinder;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class MethodChainIdentification implements ProblemDescriptorProvider {
    private static final String SHORT_DESCRIPTION = "Short description";

    private final PsiMethodCallExpression finalCall;
    private final ClassFinder classFinder;

    private MethodChainIdentification(PsiMethodCallExpression finalCall, ClassFinder classFinder) {
        this.finalCall = finalCall;
        this.classFinder = classFinder;
    }

    public static MethodChainIdentification createWithFinalCall(@NotNull PsiMethodCallExpression finalCall, @NotNull ClassFinder classFinder) {
        return new MethodChainIdentification(finalCall, classFinder);
    }

    /**
     * Gets the method call qualifier.
     * @return The method call that this identification's final call depends on, otherwise empty.
     */
    public Optional<MethodChainIdentification> getMethodCallQualifier() {
        final PsiReferenceExpression methodExpression = finalCall.getMethodExpression();
        final PsiExpression qualifierExpression = methodExpression.getQualifierExpression();

        if (qualifierExpression instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression qualifier = (PsiMethodCallExpression) qualifierExpression;
            return Optional.of(MethodChainIdentification.createWithFinalCall(qualifier, classFinder));
        } else {
            return Optional.absent();
        }
    }

    /**
     * Gets all method call qualifiers for this final call.
     * This recursively calls getMethodCallQualifier() recursively and collects all qualifiers.
     * @return A set of all recursive qualifiers
     */
    public Set<MethodChainIdentification> getAllMethodCallQualifiers() {
        Optional<MethodChainIdentification> directQualifier = getMethodCallQualifier();
        if (!directQualifier.isPresent()) {
            return new HashSet<MethodChainIdentification>();
        } else {
            Set<MethodChainIdentification> result = directQualifier.get().getAllMethodCallQualifiers();
            result.add(directQualifier.get());
            return result;
        }
    }

    public PsiType getMethodCallType() {
        return finalCall.getType();
    }

    @Override
    public ProblemDescriptor problemDescriptor(InspectionManager manager) {
        return manager.createProblemDescriptor(finalCall, finalCall, SHORT_DESCRIPTION, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
    }

    @Override
    public int hashCode() {
        return finalCall.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodChainIdentification)) {
            return false;
        }

        MethodChainIdentification other = (MethodChainIdentification) obj;
        return other.finalCall.equals(finalCall);
    }

    /**
     * Calculates how many times the type changes in the method call chain.
     * @return The number of times the type in the method call chain changes.
     */
    public int calculateTypeDifference() {
        int typeDifference = -1;
        PsiType currentType = null;

        PsiExpression currentExpression = finalCall;

        while (currentExpression != null) {
            PsiType newType = currentExpression.getType();
            if (newType != PsiType.VOID && (currentType != null ? !currentType.equals(newType) : newType != null)) {
                typeDifference++;
            }
            currentType = newType;

            if (currentExpression instanceof PsiMethodCallExpression) {
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) currentExpression).getMethodExpression();
                currentExpression = methodExpression.getQualifierExpression();
            } else {
                currentExpression = null;
            }
        }
        return typeDifference;
    }

    public PsiMethodCallExpression getFinalCall() {
        return finalCall;
    }

    /**
     * <p>Checks if the method chain contains any external calls, i.e. calls with
     * return types are not defined in the current project.</p>
     * @return True iff the method chain contains calls to externally-defined types.
     */
    public boolean containsProjectExternalCalls() {

        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(finalCall.getProject());
        PsiExpression currentExpression = finalCall;
        while (currentExpression != null) {
            PsiType newType = currentExpression.getType();
            PsiClass classForCurrentType = PsiTypesUtil.getPsiClass(newType);
            if (classForCurrentType != null) {
                String qualifiedName = classForCurrentType.getQualifiedName();
                if (qualifiedName != null && classFinder.findClass(searchScope, qualifiedName) == null) {
                    return true;
                }
            }

            if (currentExpression instanceof PsiMethodCallExpression) {
                PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) currentExpression).getMethodExpression();
                currentExpression = methodExpression.getQualifierExpression();
            } else {
                currentExpression = null;
            }
        }
        return false;
    }

}
