package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RenameMethodRefactoringStep {

    private static final String RENAME_METHOD_STEP_NAME = "Rename Method";
    private PsiMethod methodToRename;
    private Editor editor;
    private Project project;

    public RenameMethodRefactoringStep(PsiMethod methodToRename, Project project) {
        this.methodToRename = methodToRename;
        this.project = project;
        editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    /**
     * Performs the refactoring step.
     * @return The result of the refactoring
     */
    public Result process() {
        moveCaretToMethodToRename();
        launchInlineRename();
        focusOnEditorForTyping();

        return new Result(methodToRename);
    }

    /**
     * Focuses the user on the editor to enable direct typing without having to click on the editor.
     * @return An action callback that runs when the focus has been performed
     */
    private ActionCallback focusOnEditorForTyping() {
        return IdeFocusManager.getInstance(project).requestFocus(editor.getContentComponent(),true);
    }

    /**
     * Launches an inline rename process supported by IntelliJ.
     */
    private void launchInlineRename() {
        MemberInplaceRenameHandler handler = new MemberInplaceRenameHandler();
        handler.doRename(methodToRename, editor, null);
    }

    /**
     * Moves the caret to the method to rename.
     */
    private void moveCaretToMethodToRename() {
        editor.getCaretModel().moveToOffset(methodToRename.getTextOffset());
    }

    public void describeStep(Map<String, Object> templateValues) {
        Template template = new HTMLFileTemplate(template());
        templateValues.put("nextStepDescription", template.render(new HashMap<String, Object>()));
        templateValues.put("nextStepName", RENAME_METHOD_STEP_NAME);
    }

    private String template() {
        try {
            return IOUtils.readFile("descriptionTemplates/renameMethodStepDescription.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Represents a result of a rename refactoring step.
     */
    public static final class Result {
        final PsiMethod method;

        private Result(PsiMethod method) {
            this.method = method;
        }

        public PsiMethod getMethod() {
            return method;
        }
    }
}
