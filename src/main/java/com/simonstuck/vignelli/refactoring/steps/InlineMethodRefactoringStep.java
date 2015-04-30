package com.simonstuck.vignelli.refactoring.steps;


import com.intellij.openapi.application.Application;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.inline.InlineMethodDialog;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InlineMethodRefactoringStep implements RefactoringStep {
    private static final String STEP_NAME = "Inline Instance Retrieval Method";
    public static final String TEMPLATE_PATH = "descriptionTemplates/inlineMethodStepDescription.html";
    private final Project project;
    private final PsiMethod method;
    private final RefactoringStepDelegate delegate;
    private final Application application;
    private MethodRemovalWaitChecker methodRemovalWaitChecker;

    public InlineMethodRefactoringStep(@NotNull Project project, @NotNull PsiMethod method, @NotNull Application application, @NotNull RefactoringStepDelegate delegate) {
        this.project = project;
        this.method = method;
        this.delegate = delegate;
        this.application = application;
    }

    @Override
    public void startListeningForGoal() {
        methodRemovalWaitChecker = new MethodRemovalWaitChecker();
        application.addApplicationListener(methodRemovalWaitChecker);
    }

    @Override
    public Result process() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        InlineMethodDialog dialog = new InlineMethodDialog(project,method,null,editor,false);
        dialog.show();
        return new Result(true);
    }

    @Override
    public void endListeningForGoal() {
        application.removeApplicationListener(methodRemovalWaitChecker);
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
        templateValues.put(STEP_NAME_TEMPLATE_KEY, STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtils.tryReadFile(TEMPLATE_PATH));
        return template.render(new HashMap<String, Object>());
    }

    static final class Result implements RefactoringStepResult {

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
     * This checker checks if the method has been inlined yet
     *
     * <p>This is done by observing if all method calls to the method have been removed.</p>
     */
    private class MethodRemovalWaitChecker extends RefactoringStepGoalChecker {

        @Nullable
        private final PsiClass clazz;

        public MethodRemovalWaitChecker() {
            super(InlineMethodRefactoringStep.this, delegate);
            clazz = PsiTreeUtil.getParentOfType(method, PsiClass.class);
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (clazz == null) {
                return new Result(false);
            }
            final int[] callsFound = {0};
            JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
                @Override
                public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                    super.visitMethodCallExpression(expression);
                    PsiMethod resolvedMethod = expression.resolveMethod();
                    if (resolvedMethod == null || resolvedMethod == method) {
                        callsFound[0]++;
                    }
                }
            };
            clazz.accept(visitor);

            if (callsFound[0] == 0) {
                return new Result(true);
            }
            return null;
        }
    }

}
