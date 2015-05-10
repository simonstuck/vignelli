package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.psi.PsiElementCollector;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassEval;
import com.simonstuck.vignelli.evaluation.datamodel.MethodClassification;
import com.simonstuck.vignelli.evaluation.datamodel.MethodEval;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class ClassEvaluator {
    @NotNull
    private final PsiClass clazz;
    @NotNull
    private final PsiElementCollector methodCollector;
    @NotNull
    private final PsiElementEvaluator<MethodClassification> methodClassifier;

    public ClassEvaluator(@NotNull PsiClass clazz, @NotNull PsiElementCollector methodCollector, @NotNull PsiElementEvaluator<MethodClassification> methodClassifier) {
        this.clazz = clazz;
        this.methodCollector = methodCollector;
        this.methodClassifier = methodClassifier;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<ClassEval> invoke() {
        ClassEval classEval = new ClassEval(clazz.getQualifiedName());

        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = methodCollector.collectElementsOfType(clazz, PsiMethod.class);
        Iterator<PsiMethod> methodIterator = methods.iterator();
        PsiElementEvaluator.EvaluationResult.Outcome currentOutcome = PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED;

        while (methodIterator.hasNext() && currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
            PsiMethod method = methodIterator.next();

            PsiElementEvaluator.EvaluationResult<MethodEval> methodEval = evaluateMethod(classEval, method);
            currentOutcome = methodEval.getOutcome();
        }

        return new PsiElementEvaluator.EvaluationResult.Default<ClassEval>(currentOutcome, classEval);
    }

    private PsiElementEvaluator.EvaluationResult<MethodEval> evaluateMethod(ClassEval classEval, PsiMethod method) {
        MethodEvaluator methodEvaluator = new MethodEvaluator(method, methodClassifier);
        PsiElementEvaluator.EvaluationResult<MethodEval> methodEval = methodEvaluator.invoke();
        if (methodEval.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
            classEval.addMethodEval(methodEval.getEvaluation());
        }
        return methodEval;
    }
}
