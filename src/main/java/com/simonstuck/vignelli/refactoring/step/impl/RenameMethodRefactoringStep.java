package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.util.messages.MessageBusConnection;
import com.simonstuck.vignelli.inspection.RenameListener;
import com.simonstuck.vignelli.inspection.VignelliRefactoringListener;
import com.simonstuck.vignelli.psi.util.NavigationUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenameMethodRefactoringStep implements RefactoringStep {

    private static final String RENAME_METHOD_STEP_NAME = "Rename Method";
    private PsiMethod methodToRename;
    private Editor editor;
    private Project project;
    private final RenameGoalChecker renameGoalChecker;
    private final Application application;
    private MessageBusConnection messageBusConnection;

    public RenameMethodRefactoringStep(PsiMethod methodToRename, Project project, RefactoringStepDelegate delegate, Application application) {
        this.methodToRename = methodToRename;
        this.project = project;
        editor = getEditor();
        renameGoalChecker = new RenameGoalChecker(this, delegate);
        this.application = application;
        messageBusConnection = project.getMessageBus().connect();
    }

    private Editor getEditor() {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = documentManager.getDocument(methodToRename.getContainingFile());
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        Editor currentTextEditor = fileEditorManager.getSelectedTextEditor();
        if (document != null) {
            EditorFactory editorFactory = EditorFactory.getInstance();
            List<Editor> editors = new ArrayList<Editor>(Arrays.asList(editorFactory.getEditors(document)));
            if (editors.contains(currentTextEditor)) {
                return currentTextEditor;
            } else if (!editors.isEmpty()) {
                return editors.get(0);
            } else {
                return editorFactory.createEditor(document);
            }
        } else {
            // File is binary or has no associated document
            return null;
        }
    }



    @Override
    public void start() {
        NavigationUtil.navigateToElement(methodToRename);
        messageBusConnection.subscribe(VignelliRefactoringListener.RENAME_LISTENER_TOPIC, renameGoalChecker);
        application.addApplicationListener(renameGoalChecker);
    }

    @Override
    public void end() {
        application.removeApplicationListener(renameGoalChecker);
        messageBusConnection.dispose();
    }

    /**
     * Performs the refactoring step.
     */
    public void process() {
        moveCaretToMethodToRename();
        launchInlineRename();
        focusOnEditorForTyping();
    }

    /**
     * Focuses the user on the editor to enable direct typing without having to click on the editor.
     * @return An action callback that runs when the focus has been performed
     */
    private ActionCallback focusOnEditorForTyping() {
        IdeFocusManager ideFocusManager = IdeFocusManager.getInstance(project);
        return ideFocusManager.requestFocus(editor.getContentComponent(), true);
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
        NavigationUtil.navigateToElement(methodToRename, true);
        focusOnEditorForTyping();
    }

    public void describeStep(Map<String, Object> templateValues) {
        Template template = new HTMLFileTemplate(template());
        templateValues.put("nextStepDescription", template.render(new HashMap<String, Object>()));
        templateValues.put("nextStepName", RENAME_METHOD_STEP_NAME);
    }

    private String template() {
        return IOUtil.tryReadFile("descriptionTemplates/renameMethodStepDescription.html");
    }

    /**
     * Represents a result of a rename refactoring step.
     */
    public static final class Result implements RefactoringStepResult {
        final PsiMethod method;

        private Result(PsiMethod method) {
            this.method = method;
        }

        public PsiMethod getMethod() {
            return method;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private class RenameGoalChecker extends RefactoringStepGoalChecker implements RenameListener {

        private boolean renamed = false;

        public RenameGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (renamed) {
                return new Result(methodToRename);
            } else {
                return null;
            }
        }

        @Override
        public void consume(PsiElement element) {
            renamed |= element == methodToRename;
        }
    }
}
