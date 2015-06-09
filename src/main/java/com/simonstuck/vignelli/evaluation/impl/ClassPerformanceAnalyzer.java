package com.simonstuck.vignelli.evaluation.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassPerformance;
import com.simonstuck.vignelli.evaluation.datamodel.MethodPerformance;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClassPerformanceAnalyzer {
    @NotNull
    private final PsiClass clazz;

    public ClassPerformanceAnalyzer(@NotNull PsiClass clazz) {
        this.clazz = clazz;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<ClassPerformance> invoke() {
        ClassPerformance classPerformance = new ClassPerformance(clazz.getQualifiedName());

        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = Lists.newArrayList(clazz.getMethods());
        for (PsiMethod method : methods) {
            MethodPerformanceAnalyzer methodMetricsCollector = new MethodPerformanceAnalyzer(method);
            PsiElementEvaluator.EvaluationResult<MethodPerformance> methodMetrics = methodMetricsCollector.invoke();

            if (methodMetrics.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                classPerformance.addMethodPerformance(method.getName(), methodMetrics.getEvaluation());
            }
        }

        return new PsiElementEvaluator.EvaluationResult.Default<ClassPerformance>(PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED, classPerformance);
    }
}
