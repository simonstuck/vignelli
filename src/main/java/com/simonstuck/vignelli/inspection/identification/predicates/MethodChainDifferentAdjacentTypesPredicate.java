package com.simonstuck.vignelli.inspection.identification.predicates;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.intellij.psi.PsiType;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;

public class MethodChainDifferentAdjacentTypesPredicate implements Predicate<MethodChainIdentification> {
    @Override
    public boolean apply(MethodChainIdentification methodChainIdentification) {
        PsiType finalCallType = methodChainIdentification.getMethodCallType();
        Optional<MethodChainIdentification> qualifier = methodChainIdentification.getMethodCallQualifier();
        return !qualifier.isPresent() || !qualifier.get().getMethodCallType().equals(finalCallType);
    }
}