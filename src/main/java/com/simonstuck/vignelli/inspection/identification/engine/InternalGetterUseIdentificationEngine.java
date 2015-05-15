package com.simonstuck.vignelli.inspection.identification.engine;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.impl.InternalGetterUseIdentification;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InternalGetterUseIdentificationEngine {
    public Set<? extends ProblemDescriptorProvider> process(@NotNull PsiMethod method) {
        Set<ProblemDescriptorProvider> results = new HashSet<ProblemDescriptorProvider>();

        @SuppressWarnings("unchecked")
        Collection<PsiMethodCallExpression> psiMethodCallExpressions = PsiTreeUtil.collectElementsOfType(method, PsiMethodCallExpression.class);
        for (PsiMethodCallExpression expression : psiMethodCallExpressions) {
            if (isGetterCall(expression, method.getContainingClass())) {
                results.add(new InternalGetterUseIdentification(expression));
            }
        }
        return results;
    }

    private boolean isGetterCall(@NotNull PsiMethodCallExpression callExpression, @Nullable PsiClass containingClass) {
        PsiMethod calledMethod = callExpression.resolveMethod();
        return calledMethod != null && calledMethod.getContainingClass() == containingClass && PropertyUtil.isSimpleGetter(calledMethod);
    }
}
