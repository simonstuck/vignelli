package com.simonstuck.vignelli.inspection.identification.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.intellij.psi.PsiType;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChainIdentification;

import org.junit.Before;
import org.junit.Test;


public class MethodChainDifferentAdjacentTypesPredicateTest {

    private MethodChainIdentification identification;
    private MethodChainDifferentAdjacentTypesPredicate predicate;

    @Before
    public void setUp() throws Exception {
        identification = mock(MethodChainIdentification.class);
        predicate = new MethodChainDifferentAdjacentTypesPredicate();
    }

    @Test
    public void shouldReturnTrueWhenNoQualifierIsPresent() throws Exception {
        when(identification.getMethodCallType()).thenReturn(PsiType.BOOLEAN);
        when(identification.getMethodCallQualifier()).thenReturn(Optional.<MethodChainIdentification>absent());

        assertTrue(predicate.apply(identification));
        verify(identification).getMethodCallType();
        verify(identification).getMethodCallQualifier();
    }

    @Test
    public void shouldReturnFalseWhenSameTypes() throws Exception {
        MethodChainIdentification qualifierMock = mock(MethodChainIdentification.class);
        when(qualifierMock.getMethodCallType()).thenReturn(PsiType.BOOLEAN);

        when(identification.getMethodCallType()).thenReturn(PsiType.BOOLEAN);
        when(identification.getMethodCallQualifier()).thenReturn(Optional.of(qualifierMock));

        assertFalse(predicate.apply(identification));
        verify(identification).getMethodCallType();
        verify(identification).getMethodCallQualifier();
        verify(qualifierMock).getMethodCallType();
    }

    @Test
    public void shouldReturnTrueWhenTwoAdjacentCallsHaveDifferentTypes() throws Exception {
        MethodChainIdentification qualifierMock = mock(MethodChainIdentification.class);
        when(qualifierMock.getMethodCallType()).thenReturn(PsiType.BOOLEAN);

        when(identification.getMethodCallType()).thenReturn(PsiType.INT);
        when(identification.getMethodCallQualifier()).thenReturn(Optional.of(qualifierMock));

        assertTrue(predicate.apply(identification));
        verify(identification).getMethodCallType();
        verify(identification).getMethodCallQualifier();
        verify(qualifierMock).getMethodCallType();
    }
}