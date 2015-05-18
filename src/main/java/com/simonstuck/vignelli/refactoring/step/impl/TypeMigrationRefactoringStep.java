package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.typeMigration.TypeMigrationLabeler;
import com.intellij.refactoring.typeMigration.TypeMigrationRules;
import com.intellij.refactoring.typeMigration.ui.TypeMigrationDialog;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepVisitor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TypeMigrationRefactoringStep implements RefactoringStep {

    @NotNull
    private final Project project;

    @NotNull
    private PsiElement rootElement;
    @NotNull
    private final Application application;

    private final TypeMigrationGoalChecker goalChecker;
    @Nullable
    private final PsiType suggestedType;

    public TypeMigrationRefactoringStep(@NotNull Project project, @NotNull PsiElement rootElement, @NotNull Application application, @NotNull RefactoringStepDelegate delegate, @Nullable PsiType suggestedType) {
        this.project = project;
        this.rootElement = rootElement;
        this.application = application;
        this.suggestedType = suggestedType;
        goalChecker = new TypeMigrationGoalChecker(this, delegate);
    }

    @Override
    public void start() {
        application.addApplicationListener(goalChecker);
    }

    @Override
    public void end() {
        application.removeApplicationListener(goalChecker);
    }

    @Override
    public void process() {
        if (!rootElement.isValid()) {
            return;
        }
        TypeMigrationRules rules = new TypeMigrationRules(TypeMigrationLabeler.getElementType(rootElement));
        if (suggestedType != null) {
            rules.setMigrationRootType(suggestedType);
        }
        rules.setBoundScope(GlobalSearchScope.allScope(project));
        final TypeMigrationDialog typeMigrationDialog = new TypeMigrationDialog(project, rootElement, rules);
        typeMigrationDialog.show();
    }

    public void setRootElement(@NotNull PsiElement rootElement) {
        this.rootElement = rootElement;
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {

    }

    @Override
    public void accept(RefactoringStepVisitor refactoringStepVisitor) {
        refactoringStepVisitor.visitElement(this);
    }

    private class TypeMigrationGoalChecker extends RefactoringStepGoalChecker {

        public TypeMigrationGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
        }

        @Override
        public RefactoringStepResult computeResult() {
            return null;
        }
    }

    public static class Result implements RefactoringStepResult {

        @Override
        public boolean isSuccess() {
            return true;
        }
    }
}
