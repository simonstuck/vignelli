package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.introduceParameter.IntroduceParameterHandler;

public class ExtractParameterRefactoringStep {

    private Project project;
    private final PsiFile file;
    private final PsiElement element;
    private Editor editor;

    public ExtractParameterRefactoringStep(Project project, PsiFile file, PsiElement element) {
        this.project = project;
        this.file = file;
        this.element = element;
        editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    public Result process() {
        moveCaretToElement();
        launchInlineRename();
        focusOnEditorForTyping();
        return new Result();
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
     * Launches an inline rename process supported by IntelliJ.
     */
    private void launchInlineRename() {
        IntroduceParameterHandler handler = new IntroduceParameterHandler();
        handler.invoke(project, editor, file, null);
    }

    /**
     * Moves the caret to the method to rename.
     */
    private void moveCaretToElement() {
        editor.getCaretModel().moveToOffset(element.getTextOffset());
    }

    public final class Result {
    }
}
