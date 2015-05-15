package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InlineMethodCallRefactoringStep implements RefactoringStep {
    @NotNull
    private final PsiMethodCallExpression getterCall;

    @NotNull
    private final RefactoringStepDelegate delegate;

    @NotNull
    private final Application application;

    private MethodCallInlinedGoalChecker goalChecker;

    public InlineMethodCallRefactoringStep(@NotNull PsiMethodCallExpression getterCall, @NotNull RefactoringStepDelegate delegate, @NotNull Application application) {
        this.getterCall = getterCall;
        this.delegate = delegate;
        this.application = application;
        goalChecker = new MethodCallInlinedGoalChecker(this, delegate);
    }

    @Override
    public void start() {
        application.addApplicationListener(goalChecker);
    }

    @Override
    public void end() {
        application.addApplicationListener(goalChecker);
    }

    @Override
    public void process() {

    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {

    }

    private static class Result implements RefactoringStepResult {

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private class MethodCallInlinedGoalChecker extends RefactoringStepGoalChecker {

        public MethodCallInlinedGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
        }

        @Override
        public RefactoringStepResult computeResult() {
            return null;
        }
    }
}
