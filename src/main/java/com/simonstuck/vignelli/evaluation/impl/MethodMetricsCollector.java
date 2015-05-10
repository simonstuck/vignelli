package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.MethodMetrics;
import com.simonstuck.vignelli.psi.util.LineUtil;
import com.simonstuck.vignelli.psi.util.MetricsUtil;

import org.jetbrains.annotations.NotNull;

public class MethodMetricsCollector {
    @NotNull
    private final PsiMethod method;

    public MethodMetricsCollector(@NotNull PsiMethod method) {
        this.method = method;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<MethodMetrics> invoke() {
        int loc = LineUtil.countLines(method.getBody());
        int commentLines = LineUtil.countCommentLines(method.getBody());
        int cyclomaticComplexity = MetricsUtil.getCyclomaticComplexity(method);
        int numParameters = getParameterCount();
        int nestedBlockDepth = MetricsUtil.getNestedBlockDepth(method);

        MethodMetrics metrics = new MethodMetrics(loc, cyclomaticComplexity,numParameters,nestedBlockDepth,commentLines);

        return new PsiElementEvaluator.EvaluationResult.Default<MethodMetrics>(PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED, metrics);

    }

    private int getParameterCount() {
        PsiParameterList parameterList = method.getParameterList();
        return parameterList.getParametersCount();
    }
}
