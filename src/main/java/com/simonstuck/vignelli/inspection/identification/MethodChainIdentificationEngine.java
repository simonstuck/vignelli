package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.predicates.MethodChainDifferentAdjacentTypesPredicate;
import com.simonstuck.vignelli.inspection.identification.predicates.MethodChainMultipleCallsPredicate;

import java.util.HashSet;
import java.util.Set;

public class MethodChainIdentificationEngine {

    /**
     * Identifies all method chains in the given {@link PsiElement}.
     * @param element The element in which to search for method chains.
     * @return A new
     */
    public MethodChainIdentificationCollection identifyMethodChains(PsiElement element) {
        final MethodChainIdentificationCollection candidates = computeIdentificationCandidates(element);
        final MethodChainIdentificationCollection toIgnore = candidates.getQualifiersIdentificationCollection();

        return candidates
                .filterIdentifications(toIgnore)
                .filter(new MethodChainDifferentAdjacentTypesPredicate())
                .filter(new MethodChainMultipleCallsPredicate());
    }

    /**
     * Computes all the candidates that may be identified as method call chain instances.
     * @param element The element in which to search for possible identifications
     * @return A new {@link MethodChainIdentificationCollection} with all candidates
     */
    private MethodChainIdentificationCollection computeIdentificationCandidates(PsiElement element) {
        MethodChainIdentificationCollection candidates = new MethodChainIdentificationCollection();

        final Set<PsiMethodCallExpression> methodCalls = getMethodCalls(element);

        for (PsiMethodCallExpression methodCall : methodCalls) {
            candidates.add(MethodChainIdentification.createWithFinalCall(methodCall));
        }
        return candidates;
    }

    /**
     * Returns all method calls contained in the given element.
     * @param element The element in which to search for method calls.
     * @return A set of all method calls
     */
    private Set<PsiMethodCallExpression> getMethodCalls(PsiElement element) {
        final Set<PsiMethodCallExpression> methodCalls = new HashSet<PsiMethodCallExpression>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                methodCalls.add(expression);
            }
        };

        element.accept(visitor);
        return methodCalls;
    }
}