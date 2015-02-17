package com.simonstuck.vignelli.inspection.identification;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class MethodChainIdentificationCollectionTest {

    private MethodChainIdentificationCollection collection;
    private MethodChainIdentification methodChainId1;
    private MethodChainIdentification methodChainId2;

    @Before
    public void setUp() throws Exception {
        collection = new MethodChainIdentificationCollection();
        methodChainId1 = mock(MethodChainIdentification.class);
        methodChainId2 = mock(MethodChainIdentification.class);
        Set<MethodChainIdentification> qualifiers1 = new HashSet<MethodChainIdentification>();
        qualifiers1.add(mock(MethodChainIdentification.class));
        qualifiers1.add(mock(MethodChainIdentification.class));

        Set<MethodChainIdentification> qualifiers2 = new HashSet<MethodChainIdentification>();
        qualifiers2.add(mock(MethodChainIdentification.class));

        when(methodChainId1.getAllMethodCallQualifiers()).thenReturn(qualifiers1);
        when(methodChainId2.getAllMethodCallQualifiers()).thenReturn(qualifiers2);

        collection.add(methodChainId1);
        collection.add(methodChainId2);
    }

    @Test
    public void shouldReturnAllQualifiersForAllMethodChainIdentifications() throws Exception {
        MethodChainIdentificationCollection result = collection.getQualifiersIdentificationCollection();
        verify(methodChainId1).getAllMethodCallQualifiers();
        verify(methodChainId2).getAllMethodCallQualifiers();
        assertEquals(3, result.size());
    }

}