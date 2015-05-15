package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InternalGetterUseRefactoringImpl extends Refactoring {
    @NotNull
    private final PsiMethodCallExpression getterCall;
    @NotNull
    private final RefactoringTracker tracker;

    public InternalGetterUseRefactoringImpl(@NotNull PsiMethodCallExpression getterCall, @NotNull RefactoringTracker tracker) {
        this.getterCall = getterCall;
        this.tracker = tracker;
    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

    @Override
    public void nextStep() {

    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {

    }

    @Override
    public void begin() {
        tracker.add(this);
    }

    @Override
    public void complete() {
        tracker.remove(this);
    }

    @Override
    public String template() {
        return null;
    }
}
