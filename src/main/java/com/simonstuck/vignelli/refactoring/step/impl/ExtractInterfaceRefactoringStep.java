package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.refactoring.extractInterface.ExtractInterfaceHandler;
import com.intellij.util.Query;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;
import com.simonstuck.vignelli.psi.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExtractInterfaceRefactoringStep implements RefactoringStep {

    private static final String STEP_NAME = "Extract Interface for Singleton and Use it Wherever Possible";
    private static final String TEMPLATE_PATH = "descriptionTemplates/extractInterfaceStepDescription.html";

    @NotNull
    private final Project project;

    @NotNull
    private final PsiClass clazz;

    @NotNull
    private final Application application;

    @NotNull
    private final PsiClass currentClass;

    @NotNull
    private InterfaceExtractedAndUsedChecker goalChecker;

    public ExtractInterfaceRefactoringStep(@NotNull Project project, @NotNull Application application, @NotNull PsiClass clazz, @NotNull PsiClass currentClass, @NotNull RefactoringStepDelegate delegate) {
        this.project = project;
        this.clazz = clazz;
        this.application = application;
        this.currentClass = currentClass;

        goalChecker = new InterfaceExtractedAndUsedChecker(this, delegate);
    }

    @Override
    public void start() {
        application.addApplicationListener(goalChecker);
        EditorUtil.navigateToElement(clazz);
    }

    @Override
    public void end() {
        application.removeApplicationListener(goalChecker);
    }

    @Override
    public void process() {
        ExtractInterfaceHandler handler = new ExtractInterfaceHandler();
        PsiElement[] elements = new PsiElement[] { clazz };
        handler.invoke(project, elements, null);
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtil.tryReadFile(TEMPLATE_PATH));
        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("singletonClass", clazz.getName());
        content.put("thisClass", currentClass.getName());
        return template.render(content);
    }

    public static final class Result implements RefactoringStepResult {

        private final boolean success;

        public Result(boolean success) {
            this.success = success;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
    }

    private class InterfaceExtractedAndUsedChecker extends RefactoringStepGoalChecker {



        public InterfaceExtractedAndUsedChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (isAnyNullOrInvalid(clazz)) {
                return new Result(false);
            }

            if (!currentClassContainsReferencesToClassUpForInterfaceExtraction() || clazz.isInterface()) {
                return new Result(true);
            }

            return null;
        }

        private boolean currentClassContainsReferencesToClassUpForInterfaceExtraction() {
            Query<PsiReference> search = ReferencesSearch.search(clazz, new LocalSearchScope(currentClass.getScope()));
            Collection<PsiReference> references = search.findAll();

            if (!references.isEmpty()) {
                return true;
            }

            Set<PsiMethod> methods = getDefinedMethods(currentClass);
            for (PsiMethod method : methods) {
                PsiParameterList parameterList = method.getParameterList();
                if (parametersContainClassReference(parameterList, clazz)) {
                    return true;
                }
            }
            return false;
        }

        private boolean parametersContainClassReference(PsiParameterList parameterList, PsiClass theClass) {
            for (PsiParameter parameter : parameterList.getParameters()) {
                PsiTypeElement typeElement = parameter.getTypeElement();
                if (typeElement != null && PsiTypesUtil.getPsiClass(typeElement.getType()) == theClass) {
                    return true;
                }
            }
            return false;
        }
    }
}
