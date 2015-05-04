package com.simonstuck.vignelli.inspection.identification.engine;

import com.google.common.base.Predicate;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.predicate.MethodChainMultipleCallsPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MethodChainIdentificationEngine {

    public static final int TYPE_DIFFERENCE_THRESHOLD = 1;

    /**
     * Identifies all method chains in the given {@link PsiElement}.
     * @param element The element in which to search for method chains.
     * @return A new
     */
    public Set<MethodChainIdentification> identifyMethodChains(PsiElement element) {
        final Collection<MethodChainIdentification> candidates = computeIdentificationCandidates(element);
        final Collection<MethodChainIdentification> toIgnore = allMethodChainQualifiers(candidates);

        Set<MethodChainIdentification> result = new HashSet<MethodChainIdentification>();
        for (MethodChainIdentification candidate : candidates) {
            Predicate<MethodChainIdentification> multipleCallsPredicate = new MethodChainMultipleCallsPredicate();
            int typeDifference = candidate.calculateTypeDifference();
            if (!toIgnore.contains(candidate)
                    && multipleCallsPredicate.apply(candidate)
                    && typeDifference > TYPE_DIFFERENCE_THRESHOLD
                ) {
                result.add(candidate);
            }
        }
        return result;
    }

    /**
     * Computes all qualifiers for all method chains
     * @param ids The identifications for which to find the qualifiers
     * @return A collection of qualifiers
     */
    private Collection<MethodChainIdentification> allMethodChainQualifiers(Collection<MethodChainIdentification> ids) {
        Collection<MethodChainIdentification> result = new LinkedList<MethodChainIdentification>();
        for (MethodChainIdentification id : ids) {
            result.addAll(id.getAllMethodCallQualifiers());
        }
        return result;
    }

    /**
     * Computes all the candidates that may be identified as method call chain instances.
     * @param element The element in which to search for possible identifications
     * @return A new collection with all method chain identification candidates
     */
    private Collection<MethodChainIdentification> computeIdentificationCandidates(PsiElement element) {
        final Set<PsiMethodCallExpression> methodCalls = getMethodCalls(element);
        final List<MethodChainIdentification> result = new ArrayList<MethodChainIdentification>(methodCalls.size());
        for (PsiMethodCallExpression expression : methodCalls) {
            result.add(MethodChainIdentification.createWithFinalCall(expression));
        }
        return result;
    }

    /**
     * Returns all method calls contained in the given element.
     * @param element The element in which to search for method calls.
     * @return A set of all method calls
     */
    private Set<PsiMethodCallExpression> getMethodCalls(PsiElement element) {
        MethodCallCollectorElementVisitor visitor = new MethodCallCollectorElementVisitor();
        element.accept(visitor);
        return visitor.getMethodCalls();
    }
}