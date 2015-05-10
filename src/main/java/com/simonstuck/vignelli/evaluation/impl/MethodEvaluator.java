package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.MethodClassification;
import com.simonstuck.vignelli.evaluation.datamodel.MethodEval;
import com.simonstuck.vignelli.evaluation.datamodel.MethodMetrics;
import com.simonstuck.vignelli.psi.util.LineUtil;
import com.simonstuck.vignelli.psi.util.MetricsUtil;

import org.jetbrains.annotations.NotNull;

public class MethodEvaluator {
    @NotNull
    private final PsiMethod method;

    @NotNull
    private final PsiElementEvaluator<MethodClassification> classifier;

    public MethodEvaluator(@NotNull PsiMethod method, @NotNull PsiElementEvaluator<MethodClassification> classifier) {
        this.method = method;
        this.classifier = classifier;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<MethodEval> invoke() {
        int loc = LineUtil.countLines(method.getBody());
        int commentLines = LineUtil.countCommentLines(method.getBody());
        int cyclomaticComplexity = MetricsUtil.getCyclomaticComplexity(method);
        int numParameters = getParameterCount();
        int nestedBlockDepth = MetricsUtil.getNestedBlockDepth(method);

        MethodMetrics metrics = new MethodMetrics(loc, cyclomaticComplexity,numParameters,nestedBlockDepth,commentLines);
        PsiElementEvaluator.EvaluationResult<MethodClassification> classificationResult = classifier.evaluate(method);


        MethodEval methodEval = null;

        if (classificationResult.getEvaluation() != null) {
            methodEval = new MethodEval(method.getName(),metrics,classificationResult.getEvaluation());
        }

        return new PsiElementEvaluator.EvaluationResult.Default<MethodEval>(classificationResult.getOutcome(), methodEval);

    }

    private int getParameterCount() {
        PsiParameterList parameterList = method.getParameterList();
        return parameterList.getParametersCount();
    }
}
