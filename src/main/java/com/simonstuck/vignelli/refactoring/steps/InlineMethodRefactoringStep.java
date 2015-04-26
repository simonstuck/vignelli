package com.simonstuck.vignelli.refactoring.steps;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.refactoring.inline.InlineMethodDialog;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
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
        @Override
        public void childRemoved(@NotNull PsiTreeChangeEvent event) {
            super.childRemoved(event);
            PsiElement removedMethod = new PsiContainsChecker(event.getChild()).findEquivalent(method);
            if (removedMethod != null && delegate != null) {
                delegate.didFinishRefactoringStep(InlineMethodRefactoringStep.this, new Result());
            }
        }
    }

}
