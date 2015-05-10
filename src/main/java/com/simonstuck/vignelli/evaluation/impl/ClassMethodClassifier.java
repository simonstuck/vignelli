package com.simonstuck.vignelli.evaluation.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassMethodClassifications;
import com.simonstuck.vignelli.evaluation.datamodel.MethodClassification;
import com.simonstuck.vignelli.psi.util.LineUtil;
import com.simonstuck.vignelli.psi.util.MetricsUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class ClassMethodClassifier {
    @NotNull
    private final PsiClass clazz;
    @NotNull
    private final PsiElementEvaluator<MethodClassification> methodClassifier;

    public ClassMethodClassifier(@NotNull PsiClass clazz, @NotNull PsiElementEvaluator<MethodClassification> methodClassifier) {
        this.clazz = clazz;
        this.methodClassifier = methodClassifier;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<ClassMethodClassifications> invoke() {
        ClassMethodClassifications classMethodClassifications = new ClassMethodClassifications(clazz.getQualifiedName());

        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = Lists.newArrayList(clazz.getMethods());

        PsiElementEvaluator.EvaluationResult.Outcome currentOutcome = PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED;
        Iterator<PsiMethod> methodIterator = methods.iterator();
        while (methodIterator.hasNext() && currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
            PsiMethod method = methodIterator.next();
            PsiElementEvaluator.EvaluationResult<MethodClassification> evaluationResult = classify(method);
            if (evaluationResult.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                classMethodClassifications.addMethodClassification(method.getName(), evaluationResult.getEvaluation());
            }

            currentOutcome = evaluationResult.getOutcome();
        }

        return new PsiElementEvaluator.EvaluationResult.Default<ClassMethodClassifications>(currentOutcome, classMethodClassifications);
    }

    private PsiElementEvaluator.EvaluationResult<MethodClassification> classify(PsiMethod method) {
        if (isLikelySimpleMethod(method)) {
            return new PsiElementEvaluator.EvaluationResult.Default<MethodClassification>(PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED, new MethodClassification(false));
        } else {
            return methodClassifier.evaluate(method);
        }
    }

    private boolean isLikelySimpleMethod(PsiMethod method) {
        int loc = LineUtil.countLines(method.getBody());
        int nbd = MetricsUtil.getNestedBlockDepth(method);

        return loc < 3 || (loc < 10 && nbd < 3);
    }
}
