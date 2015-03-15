package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.refactoring.inline.InlineLocalHandler;
import com.intellij.refactoring.util.InlineUtil;

import java.util.HashMap;
import java.util.Map;

public class InlineVariableRefactoringStep implements RefactoringStep {

    public static final String PROJECT_ARGUMENT_KEY = "project";
    public static final String VARIABLE_TO_INLINE_ARGUMENT_KEY = "variableToInline";

    private final PsiLocalVariable variableToInline;
    private final Project project;

    public InlineVariableRefactoringStep(Map<String, Object> arguments) {
        variableToInline = (PsiLocalVariable) arguments.get(VARIABLE_TO_INLINE_ARGUMENT_KEY);
        project = (Project) arguments.get(PROJECT_ARGUMENT_KEY);
    }

    @Override
    public Map<String, Object> process() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        InlineLocalHandler.invoke(project, editor, variableToInline, null);
        return new HashMap<>();
    }
}
