package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.impl.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class TrainWreckExpressionRefactoringImpl extends Refactoring implements RefactoringStepDelegate {

    private static final Logger LOG = Logger.getInstance(TrainWreckExpressionRefactoringImpl.class.getName());

    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodTrainWreckStepDescription.html";
    private final RefactoringTracker tracker;
    private final Project project;
    private final PsiFile file;

    private RefactoringStep currentRefactoringStep;

    private ExtractMethodRefactoringStep.Result extractMethodResult;

    public TrainWreckExpressionRefactoringImpl(@NotNull Collection<PsiStatement> extractRegion, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        this.project = project;
        this.file = file;

        currentRefactoringStep = new ExtractMethodRefactoringStep(extractRegion, file, project, EXTRACT_METHOD_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
        currentRefactoringStep.start();
    }

    @Override
    public boolean hasNextStep() {
        return currentRefactoringStep != null;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        currentRefactoringStep.process();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
        currentRefactoringStep.describeStep(templateValues);
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
        return IOUtil.tryReadFile("descriptionTemplates/trainWreckRefactoring.html");
    }

    @Override
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        LOG.info("didFinishRefactoringStep!");
        currentRefactoringStep.end();

        if (step instanceof ExtractMethodRefactoringStep) {
            extractMethodResult = (ExtractMethodRefactoringStep.Result) result;
            currentRefactoringStep = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file, this);
        } else if (step instanceof IntroduceParametersForMembersRefactoringImpl) {
            currentRefactoringStep = new MoveMethodRefactoringStep(project, extractMethodResult.getExtractedMethod(), ApplicationManager.getApplication(), this);
        } else if (step instanceof MoveMethodRefactoringStep) {
            MoveMethodRefactoringStep.Result moveMethodResult = (MoveMethodRefactoringStep.Result) result;
            currentRefactoringStep = new RenameMethodRefactoringStep(moveMethodResult.getNewMethod(), project);
        } else if (step instanceof RenameMethodRefactoringStep) {
            currentRefactoringStep = null;
        }

        if (currentRefactoringStep != null) {
            currentRefactoringStep.start();
        }
        setChanged();
        notifyObservers();
    }
}
