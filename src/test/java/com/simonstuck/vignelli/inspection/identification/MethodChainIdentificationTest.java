package com.simonstuck.vignelli.inspection.identification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;

import org.junit.Before;
import org.junit.Test;

public class MethodChainIdentificationTest {

    private PsiMethodCallExpression finalCallMock;
    private MethodChainIdentification id;
    private PsiReferenceExpression refExprMock;

    @Before
    public void setUp() throws Exception {
        refExprMock = mock(PsiReferenceExpression.class);

        finalCallMock = mock(PsiMethodCallExpression.class);
        when(finalCallMock.getMethodExpression()).thenReturn(refExprMock);
        id = MethodChainIdentification.createWithFinalCall(finalCallMock);
    }

    @Test
    public void shouldUseHashCodeOfFinalCall() throws Exception {
        assertEquals(finalCallMock.hashCode(), id.hashCode());
    }

    @Test
    public void shouldBeEqualToOtherMethodChainIdentificationWithSameFinalCall() throws Exception {
        assertTrue(id.equals(MethodChainIdentification.createWithFinalCall(finalCallMock)));
    }

    @Test
    public void shouldReturnNoMethodCallQualifierForMethodCallWithNoQualifier() throws Exception {
        PsiExpression qualifierExprMock = mock(PsiExpression.class);
        when(refExprMock.getQualifierExpression()).thenReturn(qualifierExprMock);

        Optional<MethodChainIdentification> qualifier = id.getMethodCallQualifier();
        assertEquals(Optional.<MethodChainIdentification>absent(), qualifier);
        verify(finalCallMock).getMethodExpression();
    }

    @Test
    public void shouldReturnTypeOfFinalCallAsType() throws Exception {
        when(finalCallMock.getType()).thenReturn(PsiType.BOOLEAN);
        assertEquals(PsiType.BOOLEAN, id.getMethodCallType());
    }

    @Test
    public void shouldReturnImmediateQualifierForMethodCall() throws Exception {
        PsiMethodCallExpression qualifierExprMock = mock(PsiMethodCallExpression.class);
        when(refExprMock.getQualifierExpression()).thenReturn(qualifierExprMock);
        MethodChainIdentification qualifierIdentification = MethodChainIdentification.createWithFinalCall(qualifierExprMock);

        Optional<MethodChainIdentification> qualifier = id.getMethodCallQualifier();
        assertEquals(Optional.of(qualifierIdentification), qualifier);
    }

    @Test
    public void shouldReturnEmptySetForMethodCallWithoutQualifier() throws Exception {
        PsiExpression qualifierExprMock = mock(PsiExpression.class);
        when(refExprMock.getQualifierExpression()).thenReturn(qualifierExprMock);
        assertEquals(0, id.getAllMethodCallQualifiers().size());
    }
}