package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiMethodReferenceExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiClassUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.refactoring.introduceParameter.IntroduceParameterHandler;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IntroduceParameterRefactoringStep implements RefactoringStep {

    private static final String INTRODUCE_PARAMETER_STEP_NAME = "Introduce Parameter";
    private final String descriptionPath;
    private Project project;
    private final PsiFile file;
    private final PsiElement element;
    private final PsiManager psiManager;
    private final RefactoringStepDelegate delegate;
    private Editor editor;
    private final ParameterIntroducedListener parameterIntroducedListener;

    public IntroduceParameterRefactoringStep(Project project, PsiFile file, PsiElement element, String descriptionPath, PsiManager psiManager, RefactoringStepDelegate delegate) {
        this.project = project;
        this.file = file;
        this.element = element;
        this.psiManager = psiManager;
        this.delegate = delegate;
        editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        this.descriptionPath = descriptionPath;
        parameterIntroducedListener = new ParameterIntroducedListener();
    }

    @Override
    public void startListeningForGoal() {
        psiManager.addPsiTreeChangeListener(parameterIntroducedListener);
    }

    @Override
    public void endListeningForGoal() {
        psiManager.removePsiTreeChangeListener(parameterIntroducedListener);
    }

    @Override
    public Result process() {
        moveCaretToElement();

        IntroduceParameterHandler handler = new IntroduceParameterHandler();
        handler.invoke(project, editor, file, null);

        focusOnEditorForTyping();
        return new Result();
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, INTRODUCE_PARAMETER_STEP_NAME);
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
        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private class ParameterIntroducedListener extends PsiTreeChangeAdapter {
        @Override
        public void childAdded(PsiTreeChangeEvent event) {
            super.childAdded(event);
            PsiParameterList parameterList = PsiTreeUtil.getParentOfType(event.getChild(), PsiParameterList.class);

            if (event.getChild() instanceof PsiParameter && parameterList != null) {
                PsiMethod correspondingMethod = PsiTreeUtil.getParentOfType(event.getChild(), PsiMethod.class);
                if (correspondingMethod != null && correspondingMethod.getParameterList() == parameterList && delegate != null) {
                    delegate.didFinishRefactoringStep(IntroduceParameterRefactoringStep.this, new Result());
                }
            }
        }
    }
}
