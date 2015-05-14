package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.introduceParameter.IntroduceParameterHandler;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.psi.util.EditorUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntroduceParameterRefactoringStep implements RefactoringStep {

    private static final String STEP_NAME = "Introduce Parameter";
    private final String descriptionPath;
    private Project project;
    private final PsiFile file;
    private final PsiElement element;
    private final Application application;
    private Editor editor;
    private final ParameterIntroducedChecker parameterIntroducedListener;

    public IntroduceParameterRefactoringStep(Project project, PsiFile file, PsiElement element, String descriptionPath, Application application, RefactoringStepDelegate delegate) {
        this.project = project;
        this.file = file;
        this.element = element;
        this.application = application;
        FileEditorManager instance = FileEditorManager.getInstance(project);
        editor = instance.getSelectedTextEditor();
        this.descriptionPath = descriptionPath;
        parameterIntroducedListener = new ParameterIntroducedChecker(this,delegate);
    }

    @Override
    public void start() {
        application.addApplicationListener(parameterIntroducedListener);
        EditorUtil.navigateToElement(element);
    }

    @Override
    public void end() {
        application.removeApplicationListener(parameterIntroducedListener);
    }

    @Override
    public void process() {
        EditorUtil.navigateToElement(element);
        IntroduceParameterHandler handler = new IntroduceParameterHandler();
        handler.invoke(project, editor, file, null);
        EditorUtil.focusOnEditorForTyping(EditorUtil.getEditor(element));
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, STEP_NAME);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtil.tryReadFile(descriptionPath));
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("parameterElement", element.getText());

        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (clazz != null) {
            contentMap.put("currentClass", clazz.getName());
        }

        return template.render(contentMap);
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

    /**
     * This checker checks whether a parameter has been introduced to the method for the element.
     *
     * <p>This works in the following way:</p>
     * <ol>
     *     <li>Save the parent of the original element for which to introduce the parameter</li>
     *     <li>Save the original parameters</li>
     *     <li>Save the method in which to introduce the parameter</li>
     *     <li>
     *         <span>For every change:</span>
     *         <ol>
     *             <li>Get the new parameters (current - original)</li>
     *             <li>For any new parameter, check if the original expression to be introduced is given as the parameter in all calls to the method.</li>
     *             <li>If so, then the parameter has been introduced.</li>
     *         </ol>
     *     </li>
     * </ol>
     */
    public class ParameterIntroducedChecker extends RefactoringStepGoalChecker {

        @Nullable
        private final PsiMethod method;
        private final Set<PsiParameter> originalParameters = new HashSet<PsiParameter>();
        private final PsiElement elementParent;

        public ParameterIntroducedChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
            method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            elementParent = PsiTreeUtil.getParentOfType(element, PsiStatement.class);

            originalParameters.addAll(getParameters(method));
        }

        /**
         * Retrieves all parameters for a given method
         * @param method The method for which to find all parameters
         * @return A new set with all the methods parameters.
         */
        private Set<PsiParameter> getParameters(@Nullable PsiMethod method) {
            Set<PsiParameter> parameters = new HashSet<PsiParameter>();
            if (method != null) {
                PsiParameterList parameterList = method.getParameterList();
                parameters.addAll(Arrays.asList(parameterList.getParameters()));
            }
            return parameters;
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (method == null) {
                return new Result(false);
            }

            // The element to introduce as a parameter will be invalid once it has been removed from the method.
            // Therefore, if it is still valid, we cannot be done yet.
            if (element.isValid() || !method.isValid()) {
                return null;
            }

            Set<PsiParameter> newParameters = getParameters(method);
            newParameters.removeAll(originalParameters);

            for (PsiParameter newParameter : newParameters) {
                if (hasOriginalExpressionPropagatedAsArgumentToParameterInAllCalls(newParameter)) {
                    return new Result(true);
                }
            }

            return null;
        }

        /**
         * Checks if the original expression to introduce as a parameter is now passed as an argument to all calls of the method as the given parameter.
         * @param newParameter The parameter to check.
         * @return True iff it is passed, false otherwise.
         */
        private boolean hasOriginalExpressionPropagatedAsArgumentToParameterInAllCalls(PsiParameter newParameter) {
            assert method != null;

            Collection<PsiReference> allMethodUsages = ReferencesSearch.search(method, method.getUseScope()).findAll();

            for (PsiReference methodUsageRef : allMethodUsages) {
                PsiElement methodUsageElem = methodUsageRef.getElement();
                if (methodUsageElem.getParent() instanceof PsiMethodCallExpression) {
                    PsiMethodCallExpression methodCall = (PsiMethodCallExpression) methodUsageElem.getParent();
                    if (!hasOriginalExpressionBeenPassedAsParameter(methodCall, newParameter)) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Checks whether the original expression to introduce as a parameter is passed to the given method call.
         * @param methodCall The method call to search.
         * @param parameter The parameter for which to look for the expresion.
         * @return True iff the expression is passed, false otherwise.
         */
        private boolean hasOriginalExpressionBeenPassedAsParameter(PsiMethodCallExpression methodCall, PsiParameter parameter) {
            assert method != null;

            int newParameterIndex = method.getParameterList().getParameterIndex(parameter);
            PsiExpressionList argumentList = methodCall.getArgumentList();
            PsiExpression[] argumentExpressions = argumentList.getExpressions();

            // While the call/method are being edited the parameter and argument counts can be unequal.
            if (argumentExpressions.length != method.getParameterList().getParametersCount()) {
                return false;
            }

            PsiExpression injectedExpression = argumentExpressions[newParameterIndex];

            // check if injected expression is the one that we removed
            return new PsiContainsChecker().findEquivalent(injectedExpression, element) != null;
        }
    }
}
