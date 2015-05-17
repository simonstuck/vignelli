package com.simonstuck.vignelli.inspection.identification.predicate;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.intellij.psi.PsiType;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChain;

public class MethodChainDifferentAdjacentTypesPredicate implements Predicate<MethodChain> {
    @Override
    public boolean apply(MethodChain methodChain) {
        PsiType finalCallType = methodChain.getMethodCallType();
        Optional<MethodChain> qualifier = methodChain.getMethodCallQualifier();
        return !qualifier.isPresent() || !qualifier.get().getMethodCallType().equals(finalCallType);
    }
}