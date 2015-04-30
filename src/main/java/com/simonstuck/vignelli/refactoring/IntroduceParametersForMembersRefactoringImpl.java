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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class IntroduceParametersForMembersRefactoringImpl extends Refactoring {

    private static final String STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceParameterStepDescription.html";
    private final Iterator<PsiElement> memberIterator;
    private final RefactoringTracker tracker;
    private Project project;
    private PsiFile file;
    private IntroduceParameterRefactoringStep introduceParameterStep;

    public IntroduceParametersForMembersRefactoringImpl(PsiMethod method, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        this.project = project;
        this.file = file;

        @SuppressWarnings("unchecked") Collection<PsiReferenceExpression> referenceExpressions = PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);

        PsiClass clazz = method.getContainingClass();

        Collection<PsiElement> fieldParameters = new HashSet<PsiElement>();
        for (PsiReferenceExpression expression : referenceExpressions) {
            PsiElement resolvedElement = expression.resolve();
            if (isMemberField(resolvedElement) || isMemberMethod(clazz, resolvedElement)) {
                fieldParameters.add(expression);
            }
        }
        memberIterator = fieldParameters.iterator();
        prepareNextStep();
    }

    @Override
    public boolean hasNextStep() {
        return introduceParameterStep != null;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        introduceParameterStep.process();
        introduceParameterStep = null;
        prepareNextStep();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        introduceParameterStep.describeStep(templateValues);
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

    private void prepareNextStep() {
        if (memberIterator.hasNext()) {
            PsiElement next = memberIterator.next();
            introduceParameterStep = new IntroduceParameterRefactoringStep(project, file, next, STEP_DESCRIPTION_PATH, ApplicationManager.getApplication(), null);
        }
    }

    private boolean isMemberField(PsiElement resolved) {
        return resolved instanceof PsiField;
    }

    private boolean isMemberMethod(PsiClass clazz, PsiElement resolved) {
        return (resolved instanceof PsiMethod && clazz.equals(((PsiMethod) resolved).getContainingClass()));
    }
}
