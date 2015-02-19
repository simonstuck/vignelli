package com.simonstuck.vignelli.inspection.identification.predicates;

import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;

import java.util.function.Predicate;

public class MethodChainMultipleCallsPredicate implements Predicate<MethodChainIdentification> {
    @Override
    public boolean test(MethodChainIdentification methodChainIdentification) {
        return methodChainIdentification.getMethodCallQualifier().isPresent();
    }
}
