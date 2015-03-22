package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.inline.InlineLocalHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InlineVariableRefactoringStep {

    private static final Logger LOG = Logger.getInstance(InlineVariableRefactoringStep.class.getName());

    private final PsiLocalVariable variableToInline;
    private final Project project;

    public InlineVariableRefactoringStep(PsiLocalVariable variableToInline, Project project) {
        this.variableToInline = variableToInline;
        this.project = project;
    }

    public Result process() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Collection<PsiStatement> affectedStatements = getAffectedStatements(variableToInline);

        LOG.debug("Statements affected by inline process: " + affectedStatements);
        LOG.debug("About to inline variable: " + variableToInline);

        InlineLocalHandler.invoke(project, editor, variableToInline, null);
        return new Result(affectedStatements);
    }

    private Collection<PsiStatement> getAffectedStatements(PsiLocalVariable variableToInline) {
        Collection<PsiReference> references = ReferencesSearch.search(variableToInline).findAll();
        Collection<PsiStatement> affectedStatements = new ArrayList<>(references.size());
        for (PsiReference reference : references) {
            PsiStatement statement = PsiTreeUtil.getParentOfType(reference.getElement(), PsiStatement.class);
            affectedStatements.add(statement);
        }
        return affectedStatements;
    }

    public final class Result {
        private final Collection<PsiStatement> affectedStatements;

        public Result(Collection<PsiStatement> affectedStatements) {
            this.affectedStatements = affectedStatements;
        }

        public Collection<PsiStatement> getAffectedStatements() {
            return affectedStatements;
        }
    }
}
