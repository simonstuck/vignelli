package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.introduceParameter.IntroduceParameterHandler;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroduceParameterRefactoringStep {

    private static final String INTRODUCE_PARAMETER_STEP_NAME = "Introduce Parameter";
    private Project project;
    private final PsiFile file;
    private final PsiElement element;
    private Editor editor;

    public IntroduceParameterRefactoringStep(Project project, PsiFile file, PsiElement element) {
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

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", INTRODUCE_PARAMETER_STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("parameterElement", element.getText());

        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (clazz != null) {
            contentMap.put("currentClass", clazz.getText());
        }

        return template.render(contentMap);
    }

    private String template() {
        try {
            return IOUtils.readFile("descriptionTemplates/introduceParameterStepDescription.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

    public static final class Result {
    }
}
