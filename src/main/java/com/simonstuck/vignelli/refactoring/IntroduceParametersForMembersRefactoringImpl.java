package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.steps.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStepResult;

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
    public void nextStep() throws NoSuchMethodException {
        process();
    }

    @Override
    public RefactoringStepResult process() {
        if (introduceParameterStep != null) {
            return introduceParameterStep.process();
        } else {
            return null;
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
        step.endListeningForGoal();
        prepareNextStep();
        if (introduceParameterStep == null && listening) {
            delegate.didFinishRefactoringStep(this, null);
        }
    }

    private void prepareNextStep() {
        PsiElement next = getNextParameterCandidate();
        if (next != null) {
            introduceParameterStep = new IntroduceParameterRefactoringStep(project, file, next, STEP_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
            introduceParameterStep.startListeningForGoal();
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
    public void startListeningForGoal() {
        listening = true;
    }

    @Override
    public void endListeningForGoal() {
        listening = false;
    }

    private boolean isMemberField(PsiElement resolved) {
        return resolved instanceof PsiField;
    }

    private boolean isMemberMethod(PsiClass clazz, PsiElement resolved) {
        return (resolved instanceof PsiMethod && clazz.equals(((PsiMethod) resolved).getContainingClass()));
    }
}
