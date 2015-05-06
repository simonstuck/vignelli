package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class TrainWreckExpressionRefactoringImpl extends Refactoring implements RefactoringStepDelegate, RefactoringStep {

    private static final Logger LOG = Logger.getInstance(TrainWreckExpressionRefactoringImpl.class.getName());

    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodTrainWreckStepDescription.html";
    public static final String DESCRIPTION_TEMPLATE_PATH = "descriptionTemplates/trainWreckRefactoring.html";

    @NotNull
    private final RefactoringTracker tracker;
    @NotNull
    private final Project project;
    @NotNull
    private final PsiFile file;

    private RefactoringStep currentRefactoringStep;

    private ExtractMethodRefactoringStep.Result extractMethodResult;

    @Nullable
    private RefactoringStepDelegate delegate;

    public TrainWreckExpressionRefactoringImpl(@NotNull Collection<PsiStatement> extractRegion, @NotNull RefactoringTracker tracker, @NotNull Project project, @NotNull PsiFile file, @Nullable RefactoringStepDelegate delegate) {
        this.tracker = tracker;
        this.project = project;
        this.file = file;
        this.delegate = delegate;

        currentRefactoringStep = new ExtractMethodRefactoringStep(extractRegion, file, project, EXTRACT_METHOD_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
        currentRefactoringStep.start();
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
        if (currentRefactoringStep != null) {
            currentRefactoringStep.describeStep(templateValues);
        }
    }

    @Override
    public void begin() {
        tracker.add(this);
    }

    @Override
    public void complete() {
        tracker.remove(this);
        deleteObservers();
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_PATH);
    }

    @Override
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        LOG.info("didFinishRefactoringStep!");
        currentRefactoringStep.end();

        if (step instanceof ExtractMethodRefactoringStep) {
            extractMethodResult = (ExtractMethodRefactoringStep.Result) result;
            IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file, this);
            if (introduceParametersForMembersRefactoring.hasNextStep()) {
                currentRefactoringStep = introduceParametersForMembersRefactoring;
            } else {
                currentRefactoringStep = createMoveMethodRefactoringStep();
            }
        } else if (step instanceof IntroduceParametersForMembersRefactoringImpl) {
            currentRefactoringStep = createMoveMethodRefactoringStep();
        } else if (step instanceof MoveMethodRefactoringStep) {
            MoveMethodRefactoringStep.Result moveMethodResult = (MoveMethodRefactoringStep.Result) result;
            currentRefactoringStep = new RenameMethodRefactoringStep(moveMethodResult.getNewMethod(), project, this, ApplicationManager.getApplication());
        } else if (step instanceof RenameMethodRefactoringStep) {
            currentRefactoringStep = null;
            if (delegate != null) {
                delegate.didFinishRefactoringStep(this, null);
            }
        }

        if (currentRefactoringStep != null) {
            currentRefactoringStep.start();
        }
        setChanged();
        notifyObservers();
    }

    private MoveMethodRefactoringStep createMoveMethodRefactoringStep() {
        return new MoveMethodRefactoringStep(project, extractMethodResult.getExtractedMethod(), ApplicationManager.getApplication(), this);
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }

    @Override
    public void process() {
        if (hasNextStep()) {
            nextStep();
        }
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        fillTemplateValues(templateValues);
    }
}
