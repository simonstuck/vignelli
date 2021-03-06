package com.simonstuck.vignelli.inspection.identification.engine.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.engine.IdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.InternalGetterUseIdentification;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InternalGetterUseIdentificationEngine implements IdentificationEngine<InternalGetterUseIdentification> {

    @Override
    public Set<InternalGetterUseIdentification> process(@NotNull PsiElement element) {
        final PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method == null) {
            return new HashSet<InternalGetterUseIdentification>();
        } else {
            Set<InternalGetterUseIdentification> results = new HashSet<InternalGetterUseIdentification>();

            @SuppressWarnings("unchecked")
            Collection<PsiMethodCallExpression> psiMethodCallExpressions = PsiTreeUtil.collectElementsOfType(element, PsiMethodCallExpression.class);
            for (PsiMethodCallExpression expression : psiMethodCallExpressions) {
                if (isGetterCall(expression, method.getContainingClass())) {
                    results.add(new InternalGetterUseIdentification(expression));
                }
            }
            return results;
        }
    }

    private boolean isGetterCall(@NotNull PsiMethodCallExpression callExpression, @Nullable PsiClass containingClass) {
        PsiMethod calledMethod = callExpression.resolveMethod();
        return calledMethod != null && calledMethod.getContainingClass() == containingClass && PropertyUtil.isSimpleGetter(calledMethod);
    }
}
