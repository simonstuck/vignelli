package com.simonstuck.vignelli.inspection.identification.engine;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.HashSet;
import java.util.Set;

class MethodCallCollectorElementVisitor extends JavaRecursiveElementVisitor {
    private final Set<PsiMethodCallExpression> methodCallExpressions = new HashSet<PsiMethodCallExpression>();

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        super.visitMethodCallExpression(expression);
        methodCallExpressions.add(expression);
    }

    public Set<PsiMethodCallExpression> getMethodCalls() {
        return methodCallExpressions;
    }
}
