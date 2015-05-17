package com.simonstuck.vignelli.inspection.identification.predicate;

import com.google.common.base.Predicate;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChain;

public class MethodChainMultipleCallsPredicate implements Predicate<MethodChain> {
    @Override
    public boolean apply(MethodChain methodChain) {
        return methodChain.getMethodCallQualifier().isPresent();
    }
}
