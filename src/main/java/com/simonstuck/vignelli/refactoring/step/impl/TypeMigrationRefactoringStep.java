package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.refactoring.typeMigration.TypeMigrationLabeler;
import com.intellij.refactoring.typeMigration.TypeMigrationRules;
import com.intellij.refactoring.typeMigration.ui.TypeMigrationDialog;
import com.simonstuck.vignelli.psi.util.PsiElementUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepVisitor;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeMigrationRefactoringStep implements RefactoringStep {

    private static final String DESCRIPTION_PATH = "descriptionTemplates/typeMigrationStepDescription.html";
    private static final String NAME = "Type Migration to Use More General Type";
    @NotNull
    private final Project project;

    @NotNull
    private PsiElement rootElement;
    @NotNull
    private final Set<PsiType> applicableTypes;
    @NotNull
    private final Application application;

    private final TypeMigrationGoalChecker goalChecker;
    @Nullable
    private final PsiType suggestedType;

    public TypeMigrationRefactoringStep(
            @NotNull Project project,
            @NotNull PsiElement rootElement,
            @Nullable PsiType suggestedType,
            @NotNull Set<PsiType> applicableTypes,
            @NotNull RefactoringStepDelegate delegate,
            @NotNull Application application
    ) {
        this.project = project;
        this.rootElement = rootElement;
        this.applicableTypes = applicableTypes;
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
        List<String> applicableTypeStrings = new LinkedList<String>();
        for (PsiType type : applicableTypes) {
            if (type.isValid()) {
                final PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
                if (psiClass != null) {
                    applicableTypeStrings.add(psiClass.getQualifiedName());
                }
            }
        }
        HTMLFileTemplate descriptionTemplate = new HTMLFileTemplate(IOUtil.tryReadFile(DESCRIPTION_PATH));
        HashMap<String, Object> contents = new HashMap<String, Object>();
        PsiClass thisClass = PsiTreeUtil.getParentOfType(rootElement, PsiClass.class);
        if (thisClass != null) {
            contents.put("thisClass", thisClass.getName());
        }

        PsiClass otherClass = PsiTypesUtil.getPsiClass(TypeMigrationLabeler.getElementType(rootElement));
        if (otherClass != null) {
            contents.put("otherClass", otherClass.getName());
        }

        if (suggestedType != null) {
            final PsiClass psiClass = PsiTypesUtil.getPsiClass(suggestedType);
            if (psiClass != null) {
                contents.put("suggestedType", psiClass.getName());
            }
        }

        contents.put("applicableTypes", applicableTypeStrings);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, descriptionTemplate.render(contents));
        templateValues.put(STEP_NAME_TEMPLATE_KEY, NAME);
    }

    @Override
    public void accept(RefactoringStepVisitor refactoringStepVisitor) {
        refactoringStepVisitor.visitElement(this);
    }

    /**
     * This goal checker checks for completion of the type migration by waiting until no
     * more references to the old type occur in the class.
     */
    private class TypeMigrationGoalChecker extends RefactoringStepGoalChecker {

        private final PsiClass thisClass;
        private final PsiClass otherClass;

        public TypeMigrationGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
            thisClass = PsiTreeUtil.getParentOfType(rootElement, PsiClass.class);
            final PsiType elementType = TypeMigrationLabeler.getElementType(rootElement);
            otherClass = (elementType != null) ? PsiTypesUtil.getPsiClass(elementType) : null;
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (PsiElementUtil.isAnyNullOrInvalid(thisClass, otherClass)) {
                return new Result(false);
            }

            if (applicableTypes.contains(PsiTypesUtil.getClassType(otherClass))) {
                return new Result(true);
            }

            final Collection<PsiReference> otherClassReferences = ReferencesSearch.search(otherClass, new LocalSearchScope(thisClass)).findAll();
            for (PsiReference reference : otherClassReferences) {
                PsiMethod containingMethod = PsiTreeUtil.getParentOfType(reference.getElement(), PsiMethod.class);
                if (containingMethod != null && !containingMethod.hasModifierProperty(PsiModifier.STATIC)) {
                    return null;
                }
            }
            return new Result(true);
        }
    }

    public static class Result implements RefactoringStepResult {

        private final boolean success;

        public Result(boolean success) {
            this.success = success;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
    }
}
