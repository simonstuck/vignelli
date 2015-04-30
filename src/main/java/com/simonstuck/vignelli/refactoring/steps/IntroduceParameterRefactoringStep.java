package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.introduceParameter.IntroduceParameterHandler;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

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
        editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        this.descriptionPath = descriptionPath;
        parameterIntroducedListener = new ParameterIntroducedChecker(this,delegate);
    }

    @Override
    public void startListeningForGoal() {
        application.addApplicationListener(parameterIntroducedListener);
    }

    @Override
    public void endListeningForGoal() {
        application.removeApplicationListener(parameterIntroducedListener);
    }

    @Override
    public Result process() {
        moveCaretToElement();

        IntroduceParameterHandler handler = new IntroduceParameterHandler();
        handler.invoke(project, editor, file, null);

        focusOnEditorForTyping();
        return new Result(true);
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, STEP_NAME);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtils.tryReadFile(descriptionPath));
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("parameterElement", element.getText());

        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (clazz != null) {
            contentMap.put("currentClass", clazz.getText());
        }

        return template.render(contentMap);
    }


    /**
     * Focuses the user on the editor to enable direct typing without having to click on the editor.
     *
     * @return An action callback that runs when the focus has been performed
     */
    private ActionCallback focusOnEditorForTyping() {
        return IdeFocusManager.getInstance(project).requestFocus(editor.getContentComponent(), true);
    }

    /**
     * Moves the caret to the method to rename.
     */
    private void moveCaretToElement() {
        editor.getCaretModel().moveToOffset(element.getTextOffset());
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
     *             <li>For any new parameter, check all of its references in the method body</li>
     *             <li>If any of the reference's ancestors contains the original element's parent we have succeeded.</li>
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
            elementParent = element.getParent();

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
            if (isAnyNullOrInvalid(method, elementParent)) {
                return new Result(false);
            }

            Set<PsiParameter> newParameters = getParameters(method);
            newParameters.removeAll(originalParameters);

            for (PsiParameter newParameter : newParameters) {
                @SuppressWarnings("unchecked")
                Collection<PsiReferenceExpression> allReferences = PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);
                for (PsiReferenceExpression referenceExpression : allReferences) {
                    if (referenceExpression.resolve() == newParameter && PsiTreeUtil.isAncestor(elementParent, referenceExpression, false)) {
                        return new Result(true);
                    }
                }
            }

            return null;
        }
    }
}
