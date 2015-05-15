package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.inline.InlineMethodDialog;
import com.intellij.refactoring.inline.InlineRefactoringActionHandler;
import com.simonstuck.vignelli.psi.util.EditorUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class InlineMethodCallRefactoringStep implements RefactoringStep {
    @NotNull
    private final Project project;

    @NotNull
    private final PsiMethodCallExpression getterCall;

    @NotNull
    private final RefactoringStepDelegate delegate;

    @NotNull
    private final Application application;

    private final PsiMethod method;

    private MethodCallInlinedGoalChecker goalChecker;

    public InlineMethodCallRefactoringStep(@NotNull Project project, @NotNull PsiMethodCallExpression getterCall, @NotNull RefactoringStepDelegate delegate, @NotNull Application application) {
        this.project = project;
        this.getterCall = getterCall;
        this.delegate = delegate;
        this.application = application;
        method = getterCall.resolveMethod();
        goalChecker = new MethodCallInlinedGoalChecker(this, delegate);
    }

    @Override
    public void start() {
        application.addApplicationListener(goalChecker);
    }

    @Override
    public void end() {
        application.addApplicationListener(goalChecker);
    }

    @Override
    public void process() {
        Editor editor = EditorUtil.getEditor(getterCall);
        @SuppressWarnings("unchecked")
        Collection<PsiJavaCodeReferenceElement> psiJavaCodeReferenceElements = PsiTreeUtil.collectElementsOfType(getterCall, PsiJavaCodeReferenceElement.class);
        if (psiJavaCodeReferenceElements.size() != 1) {
            // Something is wrong, so we return
            return;
        }
        InlineMethodDialog dialog = new InlineMethodDialog(project, method, psiJavaCodeReferenceElements.iterator().next(), editor, true);
        dialog.show();
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, "NAME");
        templateValues.put(STEP_NAME_TEMPLATE_KEY, "DESCRIPTION");
    }

    private static class Result implements RefactoringStepResult {

        private final boolean success;

        public Result(boolean success) {
            this.success = success;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
    }

    private class MethodCallInlinedGoalChecker extends RefactoringStepGoalChecker {

        private final PsiStatement surroundingStatement;

        public MethodCallInlinedGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
            surroundingStatement = PsiTreeUtil.getParentOfType(getterCall, PsiStatement.class);
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (getterCall.isValid()) {
                return null;
            }

            PsiField fieldToInline = PropertyUtil.findPropertyFieldByMember(method);

            if (isAnyNullOrInvalid(fieldToInline, surroundingStatement)) {
                return new Result(false);
            }

            Collection<PsiReference> allFieldReferences = ReferencesSearch.search(fieldToInline, new LocalSearchScope(surroundingStatement)).findAll();
            if (!allFieldReferences.isEmpty()) {
                return new Result(true);
            }

            return null;
        }
    }
}
