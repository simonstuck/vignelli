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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TrainWreckVariableRefactoringImpl extends Refactoring implements RefactoringStepDelegate, Observer {

    public static final String TRAIN_WRECK_REFACTORING_DESCRIPTION = "Train Wreck Refactoring";
    public static final String REFACTORING_DESCRIPTION_PATH = "descriptionTemplates/trainWreckRefactoring.html";
    @Nullable
    private final PsiElement criticalTrainWreckElement;
    private final RefactoringTracker refactoringTracker;
    private final Project project;
    private final PsiFile file;
    private RefactoringStep currentRefactoringStep;

    public TrainWreckVariableRefactoringImpl(@NotNull PsiElement trainWreckElement, @Nullable PsiElement criticalTrainWreckElement, @NotNull PsiLocalVariable variable, @NotNull RefactoringTracker refactoringTracker) {
        this.criticalTrainWreckElement = criticalTrainWreckElement;
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
        if (currentRefactoringStep != null) {
            currentRefactoringStep.end();
        }
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(REFACTORING_DESCRIPTION_PATH);
    }

    @Override
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        step.end();

        // If result == null, then the result comes from the expression refactoring and we should not automatically complete.
        if (result != null && !result.isSuccess()) {
            complete();
        }

        if (step instanceof InlineVariableRefactoringStep) {
            assert result != null;
            InlineVariableRefactoringStep.Result inlineVariableResult = (InlineVariableRefactoringStep.Result) result;
            Collection<PsiStatement> extractRegion = inlineVariableResult.getAffectedStatements();
            final TrainWreckExpressionRefactoringImpl trainWreckExpressionRefactoring = new TrainWreckExpressionRefactoringImpl(extractRegion, criticalTrainWreckElement, refactoringTracker, project, file, this);
            currentRefactoringStep = trainWreckExpressionRefactoring;
            trainWreckExpressionRefactoring.addObserver(this);
        } else if (step instanceof TrainWreckExpressionRefactoringImpl) {
            currentRefactoringStep = null;
            ((TrainWreckExpressionRefactoringImpl) step).deleteObserver(this);
        }

        if (hasNextStep()) {
            currentRefactoringStep.start();
        }

        setChanged();
        notifyObservers();
    }

    @Override
    public void update(Observable observable, Object o) {
        setChanged();
        notifyObservers();
    }
}
