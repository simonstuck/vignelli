package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntroduceParametersForCriticalCallsImpl extends Refactoring implements RefactoringStepDelegate, RefactoringStep {

    private static final String STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceParameterStepDescription.html";

    @NotNull
    private final PsiMethod method;
    @NotNull
    private final PsiElement criticalCallStructure;
    private final RefactoringTracker tracker;
    private Project project;
    private PsiFile file;
    private IntroduceParameterRefactoringStep introduceParameterStep;
    private boolean listening;
    @NotNull
    private final RefactoringStepDelegate delegate;
    private final HashSet<IntroduceParameterRefactoringStep.Result> results;

    public IntroduceParametersForCriticalCallsImpl(@NotNull PsiMethod method, @NotNull PsiElement criticalCallStructure, RefactoringTracker tracker, Project project, PsiFile file, @NotNull RefactoringStepDelegate delegate) {
        this.method = method;
        this.criticalCallStructure = criticalCallStructure;
        this.tracker = tracker;
        this.project = project;
        this.file = file;
        this.delegate = delegate;

        results = new HashSet<IntroduceParameterRefactoringStep.Result>();

        prepareNextStep();
    }

    @Override
    public boolean hasNextStep() {
        return !getCriticalCalls(method).isEmpty();
    }

    @Override
    public void nextStep() {
        process();
    }

    @Override
    public void process() {
        if (introduceParameterStep != null) {
            introduceParameterStep.process();
        }
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        fillTemplateValues(templateValues);
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        if (introduceParameterStep != null) {
            introduceParameterStep.describeStep(templateValues);
        }
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

    @Override
    public synchronized void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        step.end();

        results.add((IntroduceParameterRefactoringStep.Result) result);
        if (!result.isSuccess()) {
            complete();
            //TODO: notify the delegate of the failed result here.
        }

        prepareNextStep();
        if (introduceParameterStep == null && listening) {
            delegate.didFinishRefactoringStep(this, new Result(results));
        }
    }

    private void prepareNextStep() {
        Collection<PsiElement> memberReferences = getCriticalCalls(method);
        if (!memberReferences.isEmpty()) {
            PsiElement next = memberReferences.iterator().next();
            introduceParameterStep = new IntroduceParameterRefactoringStep(project, file, next, STEP_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
            introduceParameterStep.start();
        } else {
            introduceParameterStep = null;
        }
    }

    @Override
    public void start() {
        listening = true;
    }

    @Override
    public void end() {
        listening = false;
    }

    private Collection<PsiElement> getCriticalCalls(PsiMethod method) {

        final PsiElement[] psiElements = PsiTreeUtil.collectElements(method, new PsiElementFilter() {
            @Override
            public boolean isAccepted(PsiElement element) {
                final PsiElement criticalCall = new PsiContainsChecker().findEquivalent(element, criticalCallStructure);
                return criticalCall == element;
            }
        });

        return new HashSet<PsiElement>(Arrays.asList(psiElements));
    }


    public static class Result implements RefactoringStepResult {

        private final Set<IntroduceParameterRefactoringStep.Result> results;

        public Result(Set<IntroduceParameterRefactoringStep.Result> results) {
            this.results = results;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        public Set<IntroduceParameterRefactoringStep.Result> getResults() {
            return results;
        }
    }
}
