package com.simonstuck.vignelli.inspection.identification.predicates;

import com.intellij.psi.PsiType;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;

import java.util.Optional;
import java.util.function.Predicate;

public class MethodChainDifferentAdjacentTypesPredicate implements Predicate<MethodChainIdentification> {
    @Override
    public boolean test(MethodChainIdentification methodChainIdentification) {
        PsiType finalCallType = methodChainIdentification.getMethodCallType();
        Optional<MethodChainIdentification> qualifier = methodChainIdentification.getMethodCallQualifier();
        return !qualifier.isPresent() || qualifier.get().getMethodCallType() != finalCallType;
    }
}