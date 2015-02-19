package com.simonstuck.vignelli.inspection.identification.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class MethodChainMultipleCallsPredicateTest {

    private MethodChainMultipleCallsPredicate predicate;
    private MethodChainIdentification idMock;

    @Before
    public void setUp() throws Exception {
        predicate = new MethodChainMultipleCallsPredicate();
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