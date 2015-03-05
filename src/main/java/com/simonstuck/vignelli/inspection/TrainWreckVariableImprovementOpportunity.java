package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.refactoring.inline.InlineLocalHandler;

public class TrainWreckVariableImprovementOpportunity {

    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;

    public TrainWreckVariableImprovementOpportunity(PsiElement trainWreckElement, PsiLocalVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
    }

    public void process() {
        Project project = variable.getProject();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        InlineLocalHandler.invoke(project, editor, variable, null);
    }
}
