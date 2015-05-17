package com.simonstuck.vignelli.inspection.identification.engine;

import com.google.common.base.Predicate;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChain;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.inspection.identification.predicate.MethodChainMultipleCallsPredicate;
import com.simonstuck.vignelli.psi.ClassFinder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TrainWreckIdentificationEngine {

    private final ClassFinder classFinder;


    public TrainWreckIdentificationEngine(@NotNull ClassFinder classFinder) {
        this.classFinder = classFinder;
    }

    /**
     * Identifies all method chains in the given {@link PsiElement}.
     * @param element The element in which to search for train wrecks.
     * @return A new set of train wreck identifications in the element
     */
    public Set<TrainWreckIdentification> process(PsiElement element) {
        final Collection<MethodChain> candidates = computeIdentificationCandidates(element);

        Set<MethodChain> resultChains = new HashSet<MethodChain>();
        for (MethodChain candidate : candidates) {
            Predicate<MethodChain> multipleCallsPredicate = new MethodChainMultipleCallsPredicate();
            int typeDifference = candidate.calculateTypeDifference();
            boolean containsProjectExternalCalls = candidate.containsProjectExternalCalls();
            if (!containsProjectExternalCalls && multipleCallsPredicate.apply(candidate)
                    && (typeDifference > TrainWreckIdentification.TRAIN_WRECK_TYPE_DIFFERENCE_THRESHOLD
                    || (typeDifference >= TrainWreckIdentification.TRAIN_WRECK_TYPE_DIFFERENCE_THRESHOLD && candidate.getLength() <= 3))) {
                resultChains.add(candidate);
            }
        }
        resultChains.removeAll(allMethodChainQualifiers(resultChains));

        Set<TrainWreckIdentification> trainWreckIdentifications = new HashSet<TrainWreckIdentification>();
        for (MethodChain chain : resultChains) {
            trainWreckIdentifications.add(new TrainWreckIdentification(chain.getFinalCall()));
        }
        return trainWreckIdentifications;
    }

    /**
     * Computes all qualifiers for all method chains
     * @param ids The identifications for which to find the qualifiers
     * @return A collection of qualifiers
     */
    private Collection<MethodChain> allMethodChainQualifiers(Collection<MethodChain> ids) {
        Collection<MethodChain> result = new LinkedList<MethodChain>();
        for (MethodChain id : ids) {
            result.addAll(id.getAllMethodCallQualifiers());
        }
        return result;
    }

    /**
     * Computes all the candidates that may be identified as method call chain instances.
     * @param element The element in which to search for possible identifications
     * @return A new collection with all method chain identification candidates
     */
    private Collection<MethodChain> computeIdentificationCandidates(PsiElement element) {
        final Set<PsiMethodCallExpression> methodCalls = getMethodCalls(element);
        final List<MethodChain> result = new ArrayList<MethodChain>(methodCalls.size());
        for (PsiMethodCallExpression expression : methodCalls) {
            result.add(new MethodChain(expression, classFinder));
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