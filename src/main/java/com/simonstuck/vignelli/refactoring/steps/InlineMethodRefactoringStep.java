package com.simonstuck.vignelli.refactoring.steps;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
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
    private final PsiManager psiManager;
    private PsiTreeChangeAdapter methodRemovalWaitChecker;

    public InlineMethodRefactoringStep(@NotNull Project project, @NotNull PsiMethod method, @NotNull PsiManager psiManager, @Nullable RefactoringStepDelegate delegate) {
        this.project = project;
        this.method = method;
        this.delegate = delegate;
        this.psiManager = psiManager;
    }

    @Override
    public void startListeningForGoal() {
        methodRemovalWaitChecker = new MethodRemovalWaitChecker();
        psiManager.addPsiTreeChangeListener(methodRemovalWaitChecker);
    }

    @Override
    public Result process() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        InlineMethodDialog dialog = new InlineMethodDialog(project,method,null,editor,false);
        dialog.show();
        return new Result();
    }

    @Override
    public void endListeningForGoal() {
        psiManager.removePsiTreeChangeListener(methodRemovalWaitChecker);
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

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private class MethodRemovalWaitChecker extends PsiTreeChangeAdapter {

        private final PsiClass clazz;

        public MethodRemovalWaitChecker() {
            clazz = PsiTreeUtil.getParentOfType(method, PsiClass.class);
        }

        private void performCheck() {
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

            if (callsFound[0] == 0 && delegate != null) {
                delegate.didFinishRefactoringStep(InlineMethodRefactoringStep.this, new Result());
            }

        }
        @Override
        public void childRemoved(@NotNull PsiTreeChangeEvent event) {
            super.childRemoved(event);
            performCheck();
        }

        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
            super.childrenChanged(event);
            performCheck();
        }

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            super.childAdded(event);
            performCheck();
        }

        @Override
        public void childMoved(@NotNull PsiTreeChangeEvent event) {
            super.childMoved(event);
            performCheck();
        }

        @Override
        public void childReplaced(@NotNull PsiTreeChangeEvent event) {
            super.childReplaced(event);
            performCheck();
        }
    }

}
