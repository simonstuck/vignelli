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
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InlineVariableRefactoringStep {

    private static final Logger LOG = Logger.getInstance(InlineVariableRefactoringStep.class.getName());
    public static final String INLINE_VARIABLE_STEP_NAME = "Inline Variable";

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

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", INLINE_VARIABLE_STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("variableToInline", variableToInline.getText());
        Collection<PsiStatement> affectedStatements = getAffectedStatements(variableToInline);
        if (!affectedStatements.isEmpty()) {
            PsiStatement firstAffectedStatement = affectedStatements.iterator().next();
            contentMap.put("firstAffectedStatement", firstAffectedStatement.getText());
        }
        return template.render(contentMap);
    }

    private String template() {
        try {
            return IOUtils.readFile(getClass().getResource("/descriptionTemplates/inlineStepDescription.html").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
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
