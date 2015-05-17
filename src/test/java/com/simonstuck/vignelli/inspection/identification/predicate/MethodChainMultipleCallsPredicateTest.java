package com.simonstuck.vignelli.inspection.identification.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChain;

import org.junit.Before;
import org.junit.Test;

public class MethodChainMultipleCallsPredicateTest {

    private MethodChainMultipleCallsPredicate predicate;
    private MethodChain idMock;

    @Before
    public void setUp() throws Exception {
        predicate = new MethodChainMultipleCallsPredicate();
        idMock = mock(MethodChain.class);
    }

    @Test
    public void shouldReturnTrueIfCallsQualifierIsAnotherCall() throws Exception {
        when(idMock.getMethodCallQualifier()).thenReturn(Optional.of(mock(MethodChain.class)));
        assertTrue(predicate.apply(idMock));
        verify(idMock).getMethodCallQualifier();
    }

    @Test
    public void shouldReturnFalseIfCallsQualifierisNotAnotherCall() throws Exception {
        when(idMock.getMethodCallQualifier()).thenReturn(Optional.<MethodChain>absent());
        assertFalse(predicate.apply(idMock));
        verify(idMock).getMethodCallQualifier();
    }
}