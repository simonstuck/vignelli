package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class IntroduceParametersForMembersRefactoringImpl extends Refactoring implements RefactoringStepDelegate, RefactoringStep {

    private static final String STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceParameterStepDescription.html";
    private final Iterator<PsiReferenceExpression> memberIterator;
    private final RefactoringTracker tracker;
    private Project project;
    private PsiFile file;
    private IntroduceParameterRefactoringStep introduceParameterStep;
    private boolean listening;
    @NotNull
    private final RefactoringStepDelegate delegate;

    public IntroduceParametersForMembersRefactoringImpl(PsiMethod method, RefactoringTracker tracker, Project project, PsiFile file, @NotNull RefactoringStepDelegate delegate) {
        this.tracker = tracker;
        this.project = project;
        this.file = file;
        this.delegate = delegate;

        @SuppressWarnings("unchecked") Collection<PsiReferenceExpression> referenceExpressions = PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);

        PsiClass clazz = method.getContainingClass();

        Collection<PsiReferenceExpression> memberReferences = new HashSet<PsiReferenceExpression>();
        for (PsiReferenceExpression expression : referenceExpressions) {
            PsiElement resolvedElement = expression.resolve();
            if (isMemberField(resolvedElement) || isMemberMethod(clazz, resolvedElement)) {
                memberReferences.add(expression);
            }
        }
        memberIterator = memberReferences.iterator();
        prepareNextStep();
    }

    @Override
    public boolean hasNextStep() {
        return introduceParameterStep != null;
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
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        step.end();

        if (!result.isSuccess()) {
            complete();
        }

        prepareNextStep();
        if (introduceParameterStep == null && listening) {
            delegate.didFinishRefactoringStep(this, null);
        }
    }

    private void prepareNextStep() {
        PsiElement next = getNextParameterCandidate();
        if (next != null) {
            introduceParameterStep = new IntroduceParameterRefactoringStep(project, file, next, STEP_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
            introduceParameterStep.start();
        } else {
            introduceParameterStep = null;
        }
    }

    private PsiElement getNextParameterCandidate() {
        PsiElement next = null;
        while ((next == null || !next.isValid()) && memberIterator.hasNext()) {
            next = memberIterator.next();
        }
        return next;
    }

    @Override
    public void start() {
        listening = true;
    }

    @Override
    public void end() {
        listening = false;
    }

    private boolean isMemberField(PsiElement resolved) {
        return resolved instanceof PsiField;
    }

    private boolean isMemberMethod(PsiClass clazz, PsiElement resolved) {
        return (resolved instanceof PsiMethod && clazz.equals(((PsiMethod) resolved).getContainingClass()));
    }
}
