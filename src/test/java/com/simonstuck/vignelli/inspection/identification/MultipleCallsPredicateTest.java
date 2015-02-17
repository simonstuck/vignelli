package com.simonstuck.vignelli.inspection.identification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class MultipleCallsPredicateTest {

    private MethodChainIdentification.MultipleCallsPredicate predicate;
    private MethodChainIdentification idMock;

    @Before
    public void setUp() throws Exception {
        predicate = new MethodChainIdentification.MultipleCallsPredicate();
        idMock = mock(MethodChainIdentification.class);
    }

    @Test
    public void shouldReturnTrueIfCallsQualifierIsAnotherCall() throws Exception {
        when(idMock.getMethodCallQualifier()).thenReturn(Optional.of(mock(MethodChainIdentification.class)));
        assertTrue(predicate.test(idMock));
        verify(idMock).getMethodCallQualifier();
    }

    @Test
    public void shouldReturnFalseIfCallsQualifierisNotAnotherCall() throws Exception {
        when(idMock.getMethodCallQualifier()).thenReturn(Optional.<MethodChainIdentification>empty());
        assertFalse(predicate.test(idMock));
        verify(idMock).getMethodCallQualifier();
    }
}