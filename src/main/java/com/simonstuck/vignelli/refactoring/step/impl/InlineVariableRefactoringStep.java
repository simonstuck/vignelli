package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.inline.InlineLocalHandler;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InlineVariableRefactoringStep implements RefactoringStep {

    private static final Logger LOG = Logger.getInstance(InlineVariableRefactoringStep.class.getName());
    public static final String INLINE_VARIABLE_STEP_NAME = "Inline Variable";

    private final PsiLocalVariable variableToInline;
    private final Project project;
    @NotNull
    private final Application application;
    private final InlinedVariableGoalChecker inlinedVariableGoalChecker;
    private final Collection<PsiStatement> affectedStatements;

    public InlineVariableRefactoringStep(@NotNull PsiLocalVariable variableToInline, @NotNull Project project, @NotNull Application application, @NotNull RefactoringStepDelegate delegate) {
        this.variableToInline = variableToInline;
        this.project = project;
        this.application = application;

        inlinedVariableGoalChecker = new InlinedVariableGoalChecker(this, delegate);
        affectedStatements = getAffectedStatements(variableToInline);
    }

    @Override
    public void start() {
        application.addApplicationListener(inlinedVariableGoalChecker);
    }

    @Override
    public void end() {
        application.removeApplicationListener(inlinedVariableGoalChecker);
    }

    public void process() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        LOG.debug("Statements affected by inline process: " + affectedStatements);
        LOG.debug("About to inline variable: " + variableToInline);

        InlineLocalHandler.invoke(project, editor, variableToInline, null);
    }

    private Collection<PsiStatement> getAffectedStatements(PsiLocalVariable variableToInline) {
        Collection<PsiReference> references = ReferencesSearch.search(variableToInline).findAll();
        Collection<PsiStatement> affectedStatements = new ArrayList<PsiStatement>(references.size());
        for (PsiReference reference : references) {
            PsiStatement statement = PsiTreeUtil.getParentOfType(reference.getElement(), PsiStatement.class);
            affectedStatements.add(statement);
        }
        return affectedStatements;
    }

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", INLINE_VARIABLE_STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("variableToInline", variableToInline.getText());
        if (!affectedStatements.isEmpty()) {
            PsiStatement firstAffectedStatement = affectedStatements.iterator().next();
            contentMap.put("firstAffectedStatement", firstAffectedStatement.getText());
        }
        return template.render(contentMap);
    }

    private String template() {
        return IOUtil.tryReadFile("descriptionTemplates/inlineStepDescription.html");
    }

    public static final class Result implements RefactoringStepResult {
        private final Collection<PsiStatement> affectedStatements;

        public Result(Collection<PsiStatement> affectedStatements) {
            this.affectedStatements = affectedStatements;
        }

        public Collection<PsiStatement> getAffectedStatements() {
            return affectedStatements;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    /**
     * This checker checks whether the variable to be inlined has been inlined.
     */
    private class InlinedVariableGoalChecker extends RefactoringStepGoalChecker {

        public InlinedVariableGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (!variableToInline.isValid()) {
                for (PsiStatement statement : affectedStatements) {
                    @SuppressWarnings("unchecked")
                    Collection<PsiReferenceExpression> references = PsiTreeUtil.collectElementsOfType(statement, PsiReferenceExpression.class);
                    boolean neverMentioned = true;
                    for (PsiReferenceExpression referenceExpression : references) {
                        neverMentioned &= !referenceExpression.getText().contains(variableToInline.getName());
                    }
                    if (neverMentioned) {
                        return new Result(affectedStatements);
                    }
                }
            }
            return null;
        }
    }
}
