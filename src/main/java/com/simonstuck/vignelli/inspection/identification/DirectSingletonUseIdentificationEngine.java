package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;

import org.jetbrains.annotations.Contract;

import java.util.HashSet;
import java.util.Set;

public class DirectSingletonUseIdentificationEngine {

    public static final String GET_INSTANCE_NAME = "getInstance";

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
            PsiMethod method = (PsiMethod) methodCallExpression.getMethodExpression().resolve();
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
        return method != null && method.getName().equals(GET_INSTANCE_NAME);
    }
}
