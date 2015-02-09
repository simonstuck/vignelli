package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;

public class MethodChainIdentificationEngine {

    public Identifications<MethodChainIdentification> identifyMethodChains(PsiMethod method) {
        final Identifications<MethodChainIdentification> identifications = new Identifications<MethodChainIdentification>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                if (isCallChain(expression)) {
                    System.out.println("This is a call chain!");
                }
            }

            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
            }
        };
        method.accept(visitor);

        return new Identifications<MethodChainIdentification>();
    }

    private boolean isCallChain(PsiElement element) {
        PsiClassType aClassType1 = getQualifierExpressionType(element);
        if (aClassType1 == null) {
            return false;
        }
        boolean first = true;
        while (true) {
            final PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression)element;
            final PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
            final PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
            PsiClassType expressionType = getQualifierExpressionType(qualifierExpression);
            if (!first) {
                if (expressionType == null) {
                    return !(qualifierExpression instanceof PsiMethodCallExpression &&
                            ((PsiMethodCallExpression) qualifierExpression).getMethodExpression().getQualifierExpression() == null);
                }
            }

            first = false;
            if (!aClassType1.equals(expressionType)) {
                return false;
            }

            aClassType1 = expressionType;
            element = qualifierExpression;
        }
    }

    private PsiClassType getQualifierExpressionType(PsiElement qualifier) {
        if (!(qualifier instanceof PsiMethodCallExpression)) {
            return null;
        }
        final PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression)qualifier;
        final PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
        final PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        final PsiType type = qualifierExpression != null ? qualifierExpression.getType() : null;
        return type instanceof PsiClassType ? (PsiClassType)type : null;
    }
}