package com.simonstuck.vignelli.inspection.identification;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdentificationCollectionTest {

    @Test
    public void returnsAnIteratorOverEmptySetWhenItContainsNoIdentifications() throws Exception {
        IdentificationCollection<MethodChainIdentification> ids = new IdentificationCollection<MethodChainIdentification>();
        assertFalse(ids.iterator().hasNext());
    }
}