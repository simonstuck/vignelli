package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.inspection.identification.engine.impl.TrainWreckIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepVisitor;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepVisitorAdapter;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TrainWreckExpressionRefactoringImpl extends Refactoring implements RefactoringStepDelegate, RefactoringStep {

    private static final Logger LOG = Logger.getInstance(TrainWreckExpressionRefactoringImpl.class.getName());

    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodTrainWreckStepDescription.html";
    public static final String DESCRIPTION_TEMPLATE_PATH = "descriptionTemplates/trainWreckRefactoring.html";

    private final PsiElement criticalCallStructure;
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
    private IntroduceParametersForCriticalCallsImpl.Result introduceParameterForCriticalChainResult;

    public TrainWreckExpressionRefactoringImpl(@NotNull Collection<PsiStatement> extractRegion, PsiElement criticalCallStructure, @NotNull RefactoringTracker tracker, @NotNull Project project, @NotNull PsiFile file, @Nullable RefactoringStepDelegate delegate) {
        this.criticalCallStructure = criticalCallStructure;
        this.tracker = tracker;
        this.project = project;
        this.file = file;
        this.delegate = delegate;

        currentRefactoringStep = new ExtractMethodRefactoringStep(extractRegion, file, project, EXTRACT_METHOD_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
        currentRefactoringStep.start();
    }

    /**
     * Checks if the critical call should remain where it is, i.e. should not move with the rest of the train wreck.
     * @param identification The identification to check.
     * @return True iff the critical chain should remain at its current location.
     */
    public static boolean shouldCriticalCallRemain(@NotNull TrainWreckIdentification identification) {
        return TrainWreckIdentificationEngine.isFullTrainWreck(identification.calculateTypeDifference())
                && !TrainWreckIdentificationEngine.isShortTrainWreck(identification.calculateTypeDifference(), identification.getLength());
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
        if (currentRefactoringStep != null) {
            currentRefactoringStep.end();
        }
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_PATH);
    }

    @Override
    public void didFinishRefactoringStep(RefactoringStep step, final RefactoringStepResult result) {
        LOG.info("didFinishRefactoringStep");
        step.end();

        if (result != null && !result.isSuccess()) {
            complete();
            return;
        }

        final RefactoringStepCompletionHandler stepCompletionHandler = new RefactoringStepCompletionHandler(result);
        step.accept(stepCompletionHandler);

        if (!stepCompletionHandler.hasBeenHandledSuccessfully()) {
            currentRefactoringStep = null;
        }

        if (hasNextStep()) {
            currentRefactoringStep.start();
        } else {
            notifyDelegateIfNecessary();
        }

        setChanged();
        notifyObservers();
    }

    private void notifyDelegateIfNecessary() {
        if (delegate != null) {
            LOG.info("finishing refactoring, letting delegate know.");
            delegate.didFinishRefactoringStep(TrainWreckExpressionRefactoringImpl.this, null);
        }
    }

    private boolean shouldExtractCriticalCall() {
        return criticalCallStructure != null;
    }


    @Override
    public void start() {
    }

    @Override
    public void end() {
        complete();
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

    @Override
    public void accept(RefactoringStepVisitor refactoringStepVisitor) {
        refactoringStepVisitor.visitElement(this);
    }

    private class RefactoringStepCompletionHandler extends RefactoringStepVisitorAdapter implements Observer {
        private final RefactoringStepResult result;
        private boolean success = false;

        public RefactoringStepCompletionHandler(RefactoringStepResult result) {
            this.result = result;
        }

        @Override
        public void visitElement(ExtractMethodRefactoringStep extractMethodRefactoringStep) {
            super.visitElement(extractMethodRefactoringStep);
            extractMethodResult = (ExtractMethodRefactoringStep.Result) result;
            if (extractMethodResult == null) {
                success = false;
            } else {
                if (shouldExtractCriticalCall()) {
                    initCriticalCallExtraction();
                } else {
                    IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file, TrainWreckExpressionRefactoringImpl.this);
                    if (introduceParametersForMembersRefactoring.hasNextStep()) {
                        currentRefactoringStep = introduceParametersForMembersRefactoring;
                        introduceParametersForMembersRefactoring.addObserver(this);
                    } else {
                        initMoveMethodStep();
                    }
                }
                success = true;
            }
        }

        @Override
        public void visitElement(IntroduceParametersForCriticalCallsImpl introduceParametersForCriticalCalls) {
            super.visitElement(introduceParametersForCriticalCalls);
            introduceParameterForCriticalChainResult = (IntroduceParametersForCriticalCallsImpl.Result) result;

            assert introduceParameterForCriticalChainResult != null;
            if (!introduceParameterForCriticalChainResult.isSuccess() || introduceParameterForCriticalChainResult.getResults().size() != 1) {
                success = false;
                return;
            }

            IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file, TrainWreckExpressionRefactoringImpl.this);
            if (introduceParametersForMembersRefactoring.hasNextStep()) {
                currentRefactoringStep = introduceParametersForMembersRefactoring;
                introduceParametersForMembersRefactoring.addObserver(this);
            } else {
                initMoveMethodStep();
            }
            success = true;
        }

        @Override
        public void visitElement(IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring) {
            super.visitElement(introduceParametersForMembersRefactoring);
            introduceParametersForMembersRefactoring.deleteObserver(this);
            initMoveMethodStep();
            success = true;
        }

        @Override
        public void visitElement(MoveMethodRefactoringStep moveMethodRefactoringStep) {
            super.visitElement(moveMethodRefactoringStep);
            MoveMethodRefactoringStep.Result moveMethodResult = (MoveMethodRefactoringStep.Result) result;
            if (moveMethodResult == null) {
                success = false;
            } else {
                initRenameMethodStep(moveMethodResult);
                success = true;
            }
        }

        @Override
        public void visitElement(RenameMethodRefactoringStep renameMethodRefactoringStep) {
            super.visitElement(renameMethodRefactoringStep);
            currentRefactoringStep = null;
        }

        /**
         * Checks if the step has been handled successfully.
         * @return True iff the step has been handled successfully.
         */
        public boolean hasBeenHandledSuccessfully() {
            return success;
        }


        private void initMoveMethodStep() {
            currentRefactoringStep = new TrainWreckMoveMethodRefactoringStepCreation(extractMethodResult.getExtractedMethod(), project, introduceParameterForCriticalChainResult, TrainWreckExpressionRefactoringImpl.this, new IntelliJClassFinderAdapter(project)).invoke();
        }

        private void initCriticalCallExtraction() {
            currentRefactoringStep = new IntroduceParametersForCriticalCallsImpl(extractMethodResult.getExtractedMethod(), criticalCallStructure, tracker, project, file, TrainWreckExpressionRefactoringImpl.this);
        }

        private void initRenameMethodStep(MoveMethodRefactoringStep.Result moveMethodResult) {
            currentRefactoringStep = new RenameMethodRefactoringStep(moveMethodResult.getNewMethod(), project, TrainWreckExpressionRefactoringImpl.this, ApplicationManager.getApplication());
        }

        @Override
        public void update(Observable observable, Object o) {
            setChanged();
            notifyObservers();
        }
    }
}
