package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.MethodMetrics;
import com.simonstuck.vignelli.evaluation.datamodel.MethodPerformance;
import com.simonstuck.vignelli.inspection.identification.engine.impl.ComplexMethodIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.engine.impl.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.engine.impl.TrainWreckIdentificationEngine;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;
import com.simonstuck.vignelli.psi.util.LineUtil;
import com.simonstuck.vignelli.psi.util.MetricsUtil;

import org.jetbrains.annotations.NotNull;

public class MethodPerformanceAnalyzer {
    public static final int EXPERIMENT_N = 100;
    @NotNull
    private final PsiMethod method;

    public MethodPerformanceAnalyzer(@NotNull PsiMethod method) {
        this.method = method;
    }

    @NotNull
    public PsiElementEvaluator.EvaluationResult<MethodPerformance> invoke() {

        MethodPerformance performance = new MethodPerformance();


        final TrainWreckIdentificationEngine trainWreckIdentificationEngine = new TrainWreckIdentificationEngine(new IntelliJClassFinderAdapter(method.getProject()));
        for (int i = 0; i < EXPERIMENT_N; i++) {
            final long timeBeforeTrainWreck = System.nanoTime();
            trainWreckIdentificationEngine.process(method);
            final long timeAfterTrainWreck = System.nanoTime();
            performance.addTrainWreckSpeed(timeAfterTrainWreck - timeBeforeTrainWreck);
        }


        for (int i = 0; i < EXPERIMENT_N; i++) {
            final DirectSingletonUseIdentificationEngine directSingletonUseIdentificationEngine = new DirectSingletonUseIdentificationEngine();
            final long timeBeforeSingleton = System.nanoTime();
            directSingletonUseIdentificationEngine.process(method);
            final long timeAfterSingleton = System.nanoTime();
            performance.addSingletonSpeed(timeAfterSingleton - timeBeforeSingleton);
        }


        final ComplexMethodIdentificationEngine complexMethodIdentificationEngine = new ComplexMethodIdentificationEngine();

        for (int i = 0; i < EXPERIMENT_N; i++) {
            final long timeBeforeComplex = System.nanoTime();
            complexMethodIdentificationEngine.process(method);
            final long timeAfterComplex = System.nanoTime();
            performance.addComplexMethodSpeed(timeAfterComplex - timeBeforeComplex);
        }

        return new PsiElementEvaluator.EvaluationResult.Default<MethodPerformance>(PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED, performance);

    }
}
