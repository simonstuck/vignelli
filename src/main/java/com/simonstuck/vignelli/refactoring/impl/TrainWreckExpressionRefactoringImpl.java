package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.simonstuck.vignelli.inspection.identification.engine.impl.TrainWreckIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        LOG.info("didFinishRefactoringStep!");
        currentRefactoringStep.end();

        if (result != null && !result.isSuccess()) {
            complete();
            return;
        }

        if (step instanceof ExtractMethodRefactoringStep) {
            extractMethodResult = (ExtractMethodRefactoringStep.Result) result;
            assert extractMethodResult != null;

            if (shouldExtractCriticalCall()) {
                currentRefactoringStep = new IntroduceParametersForCriticalCallsImpl(extractMethodResult.getExtractedMethod(), criticalCallStructure, tracker, project, file, this);
            } else {
                IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file, this);
                if (introduceParametersForMembersRefactoring.hasNextStep()) {
                    currentRefactoringStep = introduceParametersForMembersRefactoring;
                } else {
                    currentRefactoringStep = createMoveMethodRefactoringStep(extractMethodResult.getExtractedMethod());
                }
            }

        } else if (step instanceof IntroduceParametersForCriticalCallsImpl) {
            introduceParameterForCriticalChainResult = (IntroduceParametersForCriticalCallsImpl.Result) result;

            assert introduceParameterForCriticalChainResult != null;
            if (!introduceParameterForCriticalChainResult.isSuccess() || introduceParameterForCriticalChainResult.getResults().size() != 1) {
                complete();
                return;
            }

            IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file, this);
            if (introduceParametersForMembersRefactoring.hasNextStep()) {
                currentRefactoringStep = introduceParametersForMembersRefactoring;
            } else {
                currentRefactoringStep = createMoveMethodRefactoringStep(extractMethodResult.getExtractedMethod());
            }
        } else if (step instanceof IntroduceParametersForMembersRefactoringImpl) {
            currentRefactoringStep = createMoveMethodRefactoringStep(extractMethodResult.getExtractedMethod());
        } else if (step instanceof MoveMethodRefactoringStep) {
            MoveMethodRefactoringStep.Result moveMethodResult = (MoveMethodRefactoringStep.Result) result;
            assert moveMethodResult != null;
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

    private boolean shouldExtractCriticalCall() {
        return criticalCallStructure != null;
    }

    private MoveMethodRefactoringStep createMoveMethodRefactoringStep(PsiMethod methodToMove) {
        PsiExpression targetExpression = null;
        if (introduceParameterForCriticalChainResult != null) {
            final IntroduceParameterRefactoringStep.Result next = introduceParameterForCriticalChainResult.getResults().iterator().next();
            final Collection<PsiReference> allRefs = ReferencesSearch.search(next.getNewParameter(), new LocalSearchScope(methodToMove)).findAll();
            if (allRefs.isEmpty()) {
                complete();
                return null;
            }


            PsiExpression refExpr = null;
            final Iterator<PsiReference> referenceIterator = allRefs.iterator();
            while (referenceIterator.hasNext() && refExpr == null) {
                final PsiReference nextRef = referenceIterator.next();
                if (nextRef instanceof PsiExpression) {
                    refExpr = (PsiExpression) nextRef;
                }
            }

            targetExpression = refExpr;
        } else {
            targetExpression = getTargetExpression(methodToMove);
        }

        if (targetExpression == null) {
            complete();
            return null;
        } else {
            return new MoveMethodRefactoringStep(project, methodToMove, targetExpression, ApplicationManager.getApplication(), this);
        }
    }


    public PsiExpression getFinalQualifier(PsiExpression element) {
        if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression expression = (PsiMethodCallExpression) element;
            PsiReferenceExpression methodRefExpression = expression.getMethodExpression();
            return getFinalQualifier(methodRefExpression.getQualifierExpression());
        } else {
            return element;
        }
    }


    @Nullable
    private PsiExpression getTargetExpression(PsiMethod methodToMove) {
        TrainWreckIdentificationEngine engine = new TrainWreckIdentificationEngine(new IntelliJClassFinderAdapter(project));
        Set<TrainWreckIdentification> methodChains = engine.process(methodToMove);
        if (!methodChains.isEmpty()) {
            TrainWreckIdentification first = methodChains.iterator().next();
            return getFinalQualifier(first.getFinalCall());
        } else {
            return null;
        }
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
}
