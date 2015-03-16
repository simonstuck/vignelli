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

public class InlineVariableRefactoringStep implements RefactoringStep {


    private static final Logger LOG = Logger.getInstance(InlineVariableRefactoringStep.class.getName());
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
        Collection<PsiStatement> affectedStatements = getAffectedStatements(variableToInline);

        LOG.info("Statements affected by inline process: " + affectedStatements);
        LOG.info("About to inline variable: " + variableToInline);

        InlineLocalHandler.invoke(project, editor, variableToInline, null);
        Map<String, Object> results = new HashMap<>();
        results.put("project", project);
        results.put("inlineParents", affectedStatements);
        return results;
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
}
