package com.simonstuck.vignelli.inspection.identification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.testFramework.LightIdeaTestCase;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChainIdentification;
import com.simonstuck.vignelli.psi.ClassFinder;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;

public class MethodChainIdentificationTest extends LightIdeaTestCase {

    private PsiMethodCallExpression finalCallMock;
    private MethodChainIdentification id;
    private PsiReferenceExpression refExprMock;
    private ClassFinder classFinder;

    public void setUp() throws Exception {
        super.setUp();
        refExprMock = mock(PsiReferenceExpression.class);
        classFinder = new IntelliJClassFinderAdapter(getProject());

        finalCallMock = mock(PsiMethodCallExpression.class);
        when(finalCallMock.getMethodExpression()).thenReturn(refExprMock);
        id = MethodChainIdentification.createWithFinalCall(finalCallMock, classFinder);
    }

    public void testShouldUseHashCodeOfFinalCall() throws Exception {
        assertEquals(finalCallMock.hashCode(), id.hashCode());
    }

    public void testShouldBeEqualToOtherMethodChainIdentificationWithSameFinalCall() throws Exception {
        assertTrue(id.equals(MethodChainIdentification.createWithFinalCall(finalCallMock, classFinder)));
    }

    public void testShouldReturnNoMethodCallQualifierForMethodCallWithNoQualifier() throws Exception {
        PsiExpression qualifierExprMock = mock(PsiExpression.class);
        when(refExprMock.getQualifierExpression()).thenReturn(qualifierExprMock);

        Optional<MethodChainIdentification> qualifier = id.getMethodCallQualifier();
        assertEquals(Optional.<MethodChainIdentification>absent(), qualifier);
        verify(finalCallMock).getMethodExpression();
    }

    public void testShouldReturnTypeOfFinalCallAsType() throws Exception {
        when(finalCallMock.getType()).thenReturn(PsiType.BOOLEAN);
        assertEquals(PsiType.BOOLEAN, id.getMethodCallType());
    }

    public void testShouldReturnImmediateQualifierForMethodCall() throws Exception {
        PsiMethodCallExpression qualifierExprMock = mock(PsiMethodCallExpression.class);
        when(refExprMock.getQualifierExpression()).thenReturn(qualifierExprMock);
        MethodChainIdentification qualifierIdentification = MethodChainIdentification.createWithFinalCall(qualifierExprMock, classFinder);

        Optional<MethodChainIdentification> qualifier = id.getMethodCallQualifier();
        assertEquals(Optional.of(qualifierIdentification), qualifier);
    }

    public void testShouldReturnEmptySetForMethodCallWithoutQualifier() throws Exception {
        PsiExpression qualifierExprMock = mock(PsiExpression.class);
        when(refExprMock.getQualifierExpression()).thenReturn(qualifierExprMock);
        assertEquals(0, id.getAllMethodCallQualifiers().size());
    }

    public void testShouldReturnZeroTypeDifferenceForNoDifference() throws Exception {
        PsiReferenceExpression q1RefMock = mock(PsiReferenceExpression.class);
        PsiMethodCallExpression qualifier1ExprMock = mock(PsiMethodCallExpression.class);
        when(qualifier1ExprMock.getMethodExpression()).thenReturn(q1RefMock);
        when(qualifier1ExprMock.getType()).thenReturn(PsiType.BOOLEAN);


        when(refExprMock.getQualifierExpression()).thenReturn(qualifier1ExprMock);
        when(refExprMock.getType()).thenReturn(PsiType.BOOLEAN);
        assertEquals(0, id.calculateTypeDifference());
    }

    public void testShouldReturnOneTypeDifferenceForSimpleGetter() throws Exception {
        PsiReferenceExpression q2RefMock = mock(PsiReferenceExpression.class);
        PsiMethodCallExpression qualifier2ExprMock = mock(PsiMethodCallExpression.class);
        when(qualifier2ExprMock.getMethodExpression()).thenReturn(q2RefMock);
        when(qualifier2ExprMock.getType()).thenReturn(PsiType.BOOLEAN);

        PsiReferenceExpression q1RefMock = mock(PsiReferenceExpression.class);
        when(q1RefMock.getQualifierExpression()).thenReturn(qualifier2ExprMock);
        PsiMethodCallExpression qualifier1ExprMock = mock(PsiMethodCallExpression.class);
        when(qualifier1ExprMock.getMethodExpression()).thenReturn(q1RefMock);
        when(qualifier1ExprMock.getType()).thenReturn(PsiType.INT);


        when(refExprMock.getQualifierExpression()).thenReturn(qualifier1ExprMock);
        assertEquals(1, id.calculateTypeDifference());
    }

    public void testShouldReturnOneTypeDifferenceForBuilderBuildPattern() throws Exception {
        //ta().ta().ta().tB()
        PsiReferenceExpression q3RefMock = mock(PsiReferenceExpression.class);
        PsiMethodCallExpression qualifier3ExprMock = mock(PsiMethodCallExpression.class);
        when(qualifier3ExprMock.getMethodExpression()).thenReturn(q3RefMock);
        when(qualifier3ExprMock.getType()).thenReturn(PsiType.BOOLEAN);

        PsiReferenceExpression q2RefMock = mock(PsiReferenceExpression.class);
        when(q2RefMock.getQualifierExpression()).thenReturn(qualifier3ExprMock);
        PsiMethodCallExpression qualifier2ExprMock = mock(PsiMethodCallExpression.class);
        when(qualifier2ExprMock.getMethodExpression()).thenReturn(q2RefMock);
        when(qualifier2ExprMock.getType()).thenReturn(PsiType.BOOLEAN);

        PsiReferenceExpression q1RefMock = mock(PsiReferenceExpression.class);
        when(q1RefMock.getQualifierExpression()).thenReturn(qualifier2ExprMock);
        PsiMethodCallExpression qualifier1ExprMock = mock(PsiMethodCallExpression.class);
        when(qualifier1ExprMock.getMethodExpression()).thenReturn(q1RefMock);
        when(qualifier1ExprMock.getType()).thenReturn(PsiType.INT);


        when(refExprMock.getQualifierExpression()).thenReturn(qualifier1ExprMock);

        assertEquals(1, id.calculateTypeDifference());

    }
}