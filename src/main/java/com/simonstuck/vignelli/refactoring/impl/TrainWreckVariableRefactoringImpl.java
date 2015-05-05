package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.impl.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import java.util.Collection;
import java.util.Map;

public class TrainWreckVariableRefactoringImpl extends Refactoring implements RefactoringStepDelegate {

    public static final String TRAIN_WRECK_REFACTORING_DESCRIPTION = "Train Wreck Refactoring";
    public static final String REFACTORING_DESCRIPTION_PATH = "descriptionTemplates/trainWreckRefactoring.html";
    private final RefactoringTracker refactoringTracker;
    private final Project project;
    private final PsiFile file;
    private RefactoringStep currentRefactoringStep;

    public TrainWreckVariableRefactoringImpl(PsiElement trainWreckElement, PsiLocalVariable variable, RefactoringTracker refactoringTracker) {
        this.refactoringTracker = refactoringTracker;
        this.project = trainWreckElement.getProject();
        this.file = trainWreckElement.getContainingFile();

        currentRefactoringStep = new InlineVariableRefactoringStep(variable, project, ApplicationManager.getApplication(), this);
        currentRefactoringStep.start();
    }

    public String description() {
        return TRAIN_WRECK_REFACTORING_DESCRIPTION;
    }

    @Override
    public boolean hasNextStep() {
        return currentRefactoringStep != null;
    }

    @Override
    public void nextStep() {
        currentRefactoringStep.process();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
        if (hasNextStep()) {
            currentRefactoringStep.describeStep(templateValues);
        }
    }

    @Override
    public void begin() {
        refactoringTracker.add(this);
    }

    @Override
    public void complete() {
        refactoringTracker.remove(this);
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(REFACTORING_DESCRIPTION_PATH);
    }

    @Override
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        step.end();
        if (step instanceof InlineVariableRefactoringStep) {
            InlineVariableRefactoringStep.Result inlineVariableResult = (InlineVariableRefactoringStep.Result) result;
            Collection<PsiStatement> extractRegion = inlineVariableResult.getAffectedStatements();
            currentRefactoringStep = new TrainWreckExpressionRefactoringImpl(extractRegion, refactoringTracker, project, file, this);
        } else if (step instanceof TrainWreckExpressionRefactoringImpl) {
            currentRefactoringStep = null;
        }

        if (hasNextStep()) {
            currentRefactoringStep.start();
        }

        setChanged();
        notifyObservers();
    }
}