package com.simonstuck.vignelli.inspection.identification.engine.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.engine.IdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChain;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.psi.ClassFinder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TrainWreckIdentificationEngine implements IdentificationEngine {

    public static final int MIN_TRAIN_WRECK_LENGTH = 3;
    @NotNull
    private final ClassFinder classFinder;


    public TrainWreckIdentificationEngine(@NotNull ClassFinder classFinder) {
        this.classFinder = classFinder;
    }

    @Override
    public Set<TrainWreckIdentification> process(PsiElement element) {
        final Collection<MethodChain> candidates = computeIdentificationCandidates(element);

        Set<MethodChain> resultChains = new HashSet<MethodChain>();
        for (MethodChain candidate : candidates) {
            if (isTrainWreck(candidate)) {
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
     * Checks if the given method chain should be considered a train wreck.
     * @param methodChain The method chain to check.
     * @return True iff the method chain is a train wreck, false otherwise.
     */
    private boolean isTrainWreck(MethodChain methodChain) {
        int typeDifference = methodChain.calculateTypeDifference();
        boolean containsProjectExternalCalls = methodChain.containsProjectExternalCalls();

        return !containsProjectExternalCalls && isFullTrainWreck(typeDifference) || isShortTrainWreck(typeDifference, methodChain.getLength());
    }

    /**
     * Checks if the given train wreck is a full train wreck wreck.
     * @return True iff the given identification is a full train wreck.
     * @param typeDifference The type difference of the train wreck.
     */
    public static boolean isFullTrainWreck(int typeDifference) {
        return typeDifference > TrainWreckIdentification.TRAIN_WRECK_TYPE_DIFFERENCE_THRESHOLD;
    }

    /**
     * Checks if the given train wreck is a short train wreck.
     * <p>A short train wreck is a train which is made up of at most 3 elements and whose last call may be of type void.</p>
     * <p>This means that calls such as <code>address.getZipCode().print()</code> will be identified as a short train wreck.</p>
     * @param typeDifference The type difference of the train wreck.
     * @param trainWreckLength The length of the method chain.
     * @return True iff the given identification is a short train wreck.
     */
    public static boolean isShortTrainWreck(int typeDifference, int trainWreckLength) {
        return typeDifference >= TrainWreckIdentification.TRAIN_WRECK_TYPE_DIFFERENCE_THRESHOLD && trainWreckLength == MIN_TRAIN_WRECK_LENGTH;
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