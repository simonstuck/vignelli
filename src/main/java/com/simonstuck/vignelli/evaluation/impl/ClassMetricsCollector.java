package com.simonstuck.vignelli.evaluation.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassMetrics;
import com.simonstuck.vignelli.evaluation.datamodel.MethodMetrics;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClassMetricsCollector {
    @NotNull
    private final PsiClass clazz;

    public ClassMetricsCollector(@NotNull PsiClass clazz) {
        this.clazz = clazz;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<ClassMetrics> invoke() {
        ClassMetrics classMetrics = new ClassMetrics(clazz.getQualifiedName());

        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = Lists.newArrayList(clazz.getMethods());
        for (PsiMethod method : methods) {
            MethodMetricsCollector methodMetricsCollector = new MethodMetricsCollector(method);
            PsiElementEvaluator.EvaluationResult<MethodMetrics> methodMetrics = methodMetricsCollector.invoke();

            if (methodMetrics.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                classMetrics.addMethodMetrics(method.getName(), methodMetrics.getEvaluation());
            }
        }

        return new PsiElementEvaluator.EvaluationResult.Default<ClassMetrics>(PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED, classMetrics);
    }
}
