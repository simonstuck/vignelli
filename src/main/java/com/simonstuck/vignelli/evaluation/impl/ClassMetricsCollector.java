package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassMetrics;
import com.simonstuck.vignelli.evaluation.datamodel.MethodMetrics;
import com.simonstuck.vignelli.psi.PsiElementCollector;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClassMetricsCollector {
    @NotNull
    private final PsiClass clazz;
    @NotNull
    private final PsiElementCollector methodCollector;

    public ClassMetricsCollector(@NotNull PsiClass clazz, @NotNull PsiElementCollector methodCollector) {
        this.clazz = clazz;
        this.methodCollector = methodCollector;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<ClassMetrics> invoke() {
        ClassMetrics classMetrics = new ClassMetrics(clazz.getQualifiedName());

        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = methodCollector.collectElementsOfType(clazz, PsiMethod.class);
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
