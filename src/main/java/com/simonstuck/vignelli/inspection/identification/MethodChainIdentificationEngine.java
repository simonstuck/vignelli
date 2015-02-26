package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.predicates.MethodChainDifferentAdjacentTypesPredicate;
import com.simonstuck.vignelli.inspection.identification.predicates.MethodChainMultipleCallsPredicate;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MethodChainIdentificationEngine {

    /**
     * Identifies all method chains in the given {@link PsiElement}.
     * @param element The element in which to search for method chains.
     * @return A new
     */
    public Collection<MethodChainIdentification> identifyMethodChains(PsiElement element) {
        final Collection<MethodChainIdentification> candidates = computeIdentificationCandidates(element);
        final Collection<MethodChainIdentification> toIgnore = allMethodChainQualifiers(candidates);

        return candidates.stream()
                .filter(m -> !toIgnore.contains(m))
                .filter(new MethodChainDifferentAdjacentTypesPredicate())
                .filter(new MethodChainMultipleCallsPredicate())
                .collect(Collectors.toCollection(new MethodChainIdentificationCollectionSupplier()));
    }

    /**
     * Computes all qualifiers for all method chains
     * @param ids The identifications for which to find the qualifiers
     * @return A collection of qualifiers
     */
    private Collection<MethodChainIdentification> allMethodChainQualifiers(Collection<MethodChainIdentification> ids) {
        return ids.stream()
                    .flatMap(m -> m.getAllMethodCallQualifiers().stream())
                    .collect(Collectors.toCollection(new MethodChainIdentificationCollectionSupplier()));
    }

    /**
     * Computes all the candidates that may be identified as method call chain instances.
     * @param element The element in which to search for possible identifications
     * @return A new collection with all method chain identification candidates
     */
    private Collection<MethodChainIdentification> computeIdentificationCandidates(PsiElement element) {
        final Set<PsiMethodCallExpression> methodCalls = getMethodCalls(element);
        return methodCalls.stream().map(MethodChainIdentification::createWithFinalCall).collect(Collectors.toList());
    }

    /**
     * Returns all method calls contained in the given element.
     * @param element The element in which to search for method calls.
     * @return A set of all method calls
     */
    private Set<PsiMethodCallExpression> getMethodCalls(PsiElement element) {
        final Set<PsiMethodCallExpression> methodCalls = new HashSet<>();

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

    private static class MethodChainIdentificationCollectionSupplier implements Supplier<Collection<MethodChainIdentification>> {
        @Override
        public Collection<MethodChainIdentification> get() {
            return new LinkedList<>();
        }
    }
}