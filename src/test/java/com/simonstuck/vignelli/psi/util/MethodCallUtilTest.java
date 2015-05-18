package com.simonstuck.vignelli.psi.util;

import static org.mockito.Mockito.mock;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightIdeaTestCase;
import com.simonstuck.vignelli.testutils.IOUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;

public class MethodCallUtilTest extends LightIdeaTestCase {

    public void testReturnsMinusOneLengthForNullArgument() throws Exception {
        assertEquals(-1, MethodCallUtil.getLength(null));
    }

    public void testReturnsLengthOneForSingleMethodCall() throws Exception {
        final PsiMethodCallExpression[] psiMethodCallExpressions1 = getExpressionsOfType(PsiMethodCallExpression.class, getJavaFacade().getElementFactory().createMethodFromText(IOUtils.readFile("src/test/resources/psi/method/oneCallMethod.txt"), null));
        PsiMethodCallExpression call = psiMethodCallExpressions1[0];
        assertEquals(1, MethodCallUtil.getLength(call));
    }

    public void testReturnsLengthTwoForMethodCallOnVariable() throws Exception {
        final PsiMethodCallExpression[] psiMethodCallExpressions1 = getExpressionsOfType(PsiMethodCallExpression.class, getJavaFacade().getElementFactory().createMethodFromText(IOUtils.readFile("src/test/resources/psi/method/oneCallOnVariableMethod.txt"), null));
        PsiMethodCallExpression call = psiMethodCallExpressions1[0];
        assertEquals(2, MethodCallUtil.getLength(call));
    }

    public void testReturnsLengthTwoForTwoMethodCalls() throws Exception {
        final PsiMethodCallExpression[] psiMethodCallExpressions1 = getExpressionsOfType(PsiMethodCallExpression.class, getJavaFacade().getElementFactory().createMethodFromText(IOUtils.readFile("src/test/resources/psi/method/twoMethodCallChainMethod.txt"), null));
        PsiMethodCallExpression call = psiMethodCallExpressions1[0];
        assertEquals(2, MethodCallUtil.getLength(call));
    }

    public void testReturnsArgumentForFinalQualifierIfNoMethodCallExpression() throws Exception {
        PsiExpression mockExpr = mock(PsiExpression.class);
        assertEquals(mockExpr, MethodCallUtil.getFinalQualifier(mockExpr));
    }

    public void testReturnsReferenceExpressionForVariableOnWhichMethodIsCalledAsFinalQualifier() throws Exception {
        PsiElement rootElement = getJavaFacade().getElementFactory().createMethodFromText(IOUtils.readFile("src/test/resources/psi/method/oneCallOnVariableMethod.txt"), null);
        final PsiMethodCallExpression[] psiMethodCallExpressions1 = getExpressionsOfType(PsiMethodCallExpression.class, rootElement);
        final PsiReferenceExpression expectedFinalQualifier = getMatchingElement(getExpressionsOfType(PsiReferenceExpression.class, rootElement), "bar");
        final PsiExpression finalQualifier = MethodCallUtil.getFinalQualifier(psiMethodCallExpressions1[0]);

        assertEquals(expectedFinalQualifier, finalQualifier);
    }

    public void testReturnsMinusOneTypeDifferenceForNullArgument() throws Exception {
        assertEquals(-1, MethodCallUtil.calculateTypeDifference(null));
    }

    private <T extends PsiElement> T[] getExpressionsOfType(Class<T> type, PsiElement rootElement) throws IOException {
        @SuppressWarnings("unchecked")
        final Collection<T> foundExpressions = PsiTreeUtil.collectElementsOfType(rootElement, type);
        @SuppressWarnings("unchecked")
        final T[] foundExpressionsArr = (T[]) Array.newInstance(type, foundExpressions.size());
        return foundExpressions.toArray(foundExpressionsArr);
    }

    private <T extends PsiElement> T getMatchingElement(T[] elements, String textElementToFind) {
        for (T element : elements) {
            if (element.getText().equals(textElementToFind)) {
                return element;
            }
        }
        return null;
    }
}