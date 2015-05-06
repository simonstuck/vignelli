package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.simonstuck.vignelli.inspection.RenameListener;
import com.simonstuck.vignelli.inspection.VignelliRefactoringListener;
import com.simonstuck.vignelli.psi.util.EditorUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RenameMethodRefactoringStep implements RefactoringStep {

    private static final String RENAME_METHOD_STEP_NAME = "Rename Method";
    public static final String DESCRIPTION_TEMPLATE_PATH = "descriptionTemplates/renameMethodStepDescription.html";

    @NotNull
    private PsiMethod methodToRename;
    @NotNull
    private final RenameGoalChecker renameGoalChecker;
    @NotNull
    private final Application application;
    @NotNull
    private MessageBusConnection messageBusConnection;

    public RenameMethodRefactoringStep(@NotNull PsiMethod methodToRename, @NotNull Project project, @NotNull RefactoringStepDelegate delegate, @NotNull Application application) {
        this.methodToRename = methodToRename;
        renameGoalChecker = new RenameGoalChecker(this, delegate);
        this.application = application;
        MessageBus messageBus = project.getMessageBus();
        messageBusConnection = messageBus.connect();
    }

    @Override
    public void start() {
        EditorUtil.navigateToElement(methodToRename);
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
        EditorUtil.focusOnEditorForTyping(EditorUtil.getEditor(methodToRename));
    }

    /**
     * Launches an inline rename process supported by IntelliJ.
     */
    private void launchInlineRename() {
        MemberInplaceRenameHandler handler = new MemberInplaceRenameHandler();
        handler.doRename(methodToRename, EditorUtil.getEditor(methodToRename), null);
    }

    /**
     * Moves the caret to the method to rename.
     */
    private void moveCaretToMethodToRename() {
        EditorUtil.navigateToElement(methodToRename, true);
        EditorUtil.focusOnEditorForTyping(EditorUtil.getEditor(methodToRename));
    }

    public void describeStep(Map<String, Object> templateValues) {
        Template template = new HTMLFileTemplate(IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_PATH));
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, template.render(new HashMap<String, Object>()));
        templateValues.put(STEP_NAME_TEMPLATE_KEY, RENAME_METHOD_STEP_NAME);
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
