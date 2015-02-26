package com.simonstuck.vignelli.inspection.identification;

import java.util.function.Predicate;

public class MethodChainIdentificationCollection extends IdentificationCollection<MethodChainIdentification> {

    /**
     * Computes the collection of method call chain identifications that act as qualifiers for
     * any of the method calls in this collection. If a method call a() is identified and depends
     * on b() which depends on c(), then b() and c() are both in the returned collection.
     * @return A new {@link MethodChainIdentificationCollection}
     */
    public MethodChainIdentificationCollection getQualifiersIdentificationCollection() {
        final MethodChainIdentificationCollection result = new MethodChainIdentificationCollection();

        for (MethodChainIdentification identification : this) {
            result.addAll(identification.getAllMethodCallQualifiers());
        }
        return result;
    }

    @Override
    public MethodChainIdentificationCollection filter(Predicate<MethodChainIdentification> predicate) {
        return super.filterWithReturnType(predicate, MethodChainIdentificationCollection.class);
    }

    @Override
    public MethodChainIdentificationCollection filterIdentifications(IdentificationCollection<MethodChainIdentification> toRemove) {
        return super.filterIdentificationsWithReturnType(toRemove, MethodChainIdentificationCollection.class);
    }
}
