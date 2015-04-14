package com.simonstuck.vignelli.inspection.identification.predicates;

import com.google.common.base.Predicate;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;

public class MethodChainMultipleCallsPredicate implements Predicate<MethodChainIdentification> {
    @Override
    public boolean apply(MethodChainIdentification methodChainIdentification) {
        return methodChainIdentification.getMethodCallQualifier().isPresent();
    }
}
