package com.simonstuck.vignelli.inspection.identification;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdentificationsTest {

    @Test
    public void returnsAnIteratorOverEmptySetWhenItContainsNoIdentifications() throws Exception {
        Identifications<MethodChainIdentification> ids = new Identifications<MethodChainIdentification>();
        assertFalse(ids.iterator().hasNext());
    }
}