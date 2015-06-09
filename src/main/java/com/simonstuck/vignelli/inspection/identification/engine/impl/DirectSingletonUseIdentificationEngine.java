package com.simonstuck.vignelli.inspection.identification.engine.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.identification.engine.IdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.DirectSingletonUseIdentification;

import org.jetbrains.annotations.Contract;

import java.util.HashSet;
import java.util.Set;

public class DirectSingletonUseIdentificationEngine implements IdentificationEngine<DirectSingletonUseIdentification> {

    public static final String GET_INSTANCE_NAME = "getInstance";

    @Override
    public Set<DirectSingletonUseIdentification> process(PsiElement element) {
        final Set<PsiMethodCallExpression> methodCallExpressions = getMethodCalls(element);
        Set<PsiMethodCallExpression> staticCalls = getStaticMethodCalls(methodCallExpressions);
        Set<PsiMethodCallExpression> instanceRetrievalCalls = getLikelyInstanceRetrievalMethods(staticCalls);

        return createDirectSingletonUseIdentifications(instanceRetrievalCalls);
    }

    private Set<DirectSingletonUseIdentification> createDirectSingletonUseIdentifications(Set<PsiMethodCallExpression> instanceRetrievalCalls) {
        final Set<DirectSingletonUseIdentification> result = new HashSet<DirectSingletonUseIdentification>();
        for (PsiMethodCallExpression call : instanceRetrievalCalls) {
            result.add(new DirectSingletonUseIdentification(call));
        }
        return result;
    }

    private Set<PsiMethodCallExpression> getStaticMethodCalls(Set<PsiMethodCallExpression> methodCallExpressions) {
        final Set<PsiMethodCallExpression> staticCalls = new HashSet<PsiMethodCallExpression>();
        for (PsiMethodCallExpression methodCall : methodCallExpressions) {
            PsiMethod me = (PsiMethod)methodCall.getMethodExpression().resolve();
            if (isStaticMethod(me)) {
                staticCalls.add(methodCall);
            }
        }
        return staticCalls;
    }

    private Set<PsiMethodCallExpression> getLikelyInstanceRetrievalMethods(Set<PsiMethodCallExpression> methodCallExpressions) {
        final Set<PsiMethodCallExpression> instanceRetrievalCalls = new HashSet<PsiMethodCallExpression>();
        for (PsiMethodCallExpression methodCallExpression : methodCallExpressions) {
            PsiMethod method = methodCallExpression.resolveMethod();
            if (isLikelyInstanceRetrievalMethod(method)) {
                instanceRetrievalCalls.add(methodCallExpression);
            }
        }
        return instanceRetrievalCalls;
    }

    private Set<PsiMethodCallExpression> getMethodCalls(PsiElement element) {
        MethodCallCollectorElementVisitor visitor = new MethodCallCollectorElementVisitor();
        element.accept(visitor);
        return visitor.getMethodCalls();
    }

    @Contract("null -> false")
    private boolean isStaticMethod(PsiMethod me) {
        return me != null && me.hasModifierProperty(PsiModifier.STATIC);
    }

    @Contract("null -> false")
    private boolean isLikelyInstanceRetrievalMethod(PsiMethod method) {
        if (method == null) {
            return false;
        }

        PsiClass containingClazz = PsiTreeUtil.getParentOfType(method, PsiClass.class);

        return containingClazz != null
                && !hasNonPrivateConstructor(containingClazz)
                && method.getName().equals(GET_INSTANCE_NAME)
                && (method.getParameterList().getParametersCount() == 0);
    }

    /**
     * Checks if the given class has a non-private constructor. Returns true iff it has the default constructor.
     * @param clazz The class to check
     * @return True iff the class has a non-private constructor.
     */
    private boolean hasNonPrivateConstructor(PsiClass clazz) {
        final PsiMethod[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            return true;
        }

        for (PsiMethod constructor : constructors) {
            if (!constructor.hasModifierProperty(PsiModifier.PRIVATE)) {
                return true;
            }
        }
        return false;
    }
}
