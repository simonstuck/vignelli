package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.impl.InlineMethodCallRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InternalGetterUseRefactoringImpl extends Refactoring implements RefactoringStepDelegate {

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/internalGetterUseRefactoring.html";

    @NotNull
    private final PsiMethodCallExpression getterCall;
    @NotNull
    private final RefactoringTracker tracker;
    private RefactoringStep inlineGetterCallStep;

    public InternalGetterUseRefactoringImpl(@NotNull PsiMethodCallExpression getterCall, @NotNull RefactoringTracker tracker) {
        this.getterCall = getterCall;
        this.tracker = tracker;

        inlineGetterCallStep = new InlineMethodCallRefactoringStep(getterCall, this, ApplicationManager.getApplication());
        inlineGetterCallStep.start();
    }

    @Override
    public boolean hasNextStep() {
        return inlineGetterCallStep != null;
    }

    @Override
    public void nextStep() {
        if (hasNextStep()) {
            inlineGetterCallStep.process();
        }
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        if (hasNextStep()) {
            inlineGetterCallStep.describeStep(templateValues);
        }
    }

    @Override
    public void begin() {
        tracker.add(this);
    }

    @Override
    public void complete() {
        tracker.remove(this);
        if (hasNextStep()) {
            inlineGetterCallStep.end();
            inlineGetterCallStep = null;
        }
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE);
    }

    @Override
    public synchronized void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        inlineGetterCallStep.end();
        inlineGetterCallStep = null;
    }
}
