package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepVisitor;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntroduceParametersForMembersRefactoringImpl extends Refactoring implements RefactoringStepDelegate, RefactoringStep {

    private static final String STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceParameterStepDescription.html";

    @NotNull
    private final PsiMethod method;
    private final RefactoringTracker tracker;
    private Project project;
    private PsiFile file;
    private IntroduceParameterRefactoringStep introduceParameterStep;
    private boolean listening;
    @NotNull
    private final RefactoringStepDelegate delegate;

    public IntroduceParametersForMembersRefactoringImpl(@NotNull PsiMethod method, RefactoringTracker tracker, Project project, PsiFile file, @NotNull RefactoringStepDelegate delegate) {
        this.method = method;
        this.tracker = tracker;
        this.project = project;
        this.file = file;
        this.delegate = delegate;

        prepareNextStep();
    }

    @Override
    public boolean hasNextStep() {
        return !getMemberReferences(method).isEmpty();
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
    public void accept(RefactoringStepVisitor refactoringStepVisitor) {
        refactoringStepVisitor.visitElement(this);
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

        if (!result.isSuccess()) {
            complete();
            //TODO: notify the delegate of the failed result here.
        }

        prepareNextStep();
        if (introduceParameterStep == null && listening) {
            delegate.didFinishRefactoringStep(this, null);
        }

        setChanged();
        notifyObservers();
    }

    private void prepareNextStep() {
        Collection<PsiReferenceExpression> memberReferences = getMemberReferences(method);
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

    private Collection<PsiReferenceExpression> getMemberReferences(PsiMethod method) {
        @SuppressWarnings("unchecked") Collection<PsiReferenceExpression> referenceExpressions = PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);

        PsiClass clazz = method.getContainingClass();

        Collection<PsiReferenceExpression> memberReferences = new HashSet<PsiReferenceExpression>();
        for (PsiReferenceExpression expression : referenceExpressions) {
            PsiElement resolvedElement = expression.resolve();
            if (isMemberField(clazz, resolvedElement) || isMemberMethod(clazz, resolvedElement)) {
                memberReferences.add(expression);
            }
        }
        return memberReferences;
    }

    private boolean isMemberField(PsiClass clazz, PsiElement resolved) {
        Set<PsiElement> allFields = new HashSet<PsiElement>(Arrays.asList(clazz.getAllFields()));
        return allFields.contains(resolved);
    }

    private boolean isMemberMethod(PsiClass clazz, PsiElement resolved) {
        Set<PsiElement> allMethods = new HashSet<PsiElement>(Arrays.asList(clazz.getAllMethods()));
        return allMethods.contains(resolved);
    }
}