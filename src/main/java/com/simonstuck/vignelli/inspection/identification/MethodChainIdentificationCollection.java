package com.simonstuck.vignelli.inspection.identification;

import java.util.function.Predicate;

public class MethodChainIdentificationCollection extends IdentificationCollection<MethodChainIdentification> {

    public MethodChainIdentificationCollection getQualifiersIdentificationCollection() {
        final MethodChainIdentificationCollection result = new MethodChainIdentificationCollection();

        for (MethodChainIdentification iden : this) {
            result.addAll(iden.getAllMethodCallQualifiers());
        }
        return result;
    }

    @Override
    public MethodChainIdentificationCollection filter(Predicate<MethodChainIdentification> predicate) {
        return super.filter(predicate, MethodChainIdentificationCollection.class);
    }

    @Override
    public MethodChainIdentificationCollection filterIdentifications(IdentificationCollection<MethodChainIdentification> toRemove) {
        return super.filterIdentifications(toRemove, MethodChainIdentificationCollection.class);
    }
}
