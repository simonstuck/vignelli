package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.introduceField.IntroduceFieldHandler;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConvertToConstructorAssignedFieldRefactoringStep {
    private static final String STEP_NAME = "Convert Expression to Constructor-Initialised Field";
    private static final String TEMPLATE_PATH = "descriptionTemplates/convertToConstructorAssignedFieldDescription.html";
    private final Project project;
    private final PsiExpression expression;

    public ConvertToConstructorAssignedFieldRefactoringStep(PsiExpression expression, Project project) {
        this.expression = expression;
        this.project = project;
    }

    public Result process() {
        PsiElement[] elements = new PsiElement[] { expression };
        ExpressionMovedToConstructorListener adapter = new ExpressionMovedToConstructorListener();

        PsiManager manager = PsiManager.getInstance(project);
        manager.addPsiTreeChangeListener(adapter);

        IntroduceFieldHandler handler = new IntroduceFieldHandler();
        handler.invoke(project, elements, null);
        manager.removePsiTreeChangeListener(adapter);

        return new Result(adapter.constructorExpression);
    }

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        PsiClass thisClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
        if (thisClass != null) {
            contentMap.put("thisClass", thisClass.getName());
        }

        return template.render(contentMap);
    }

    private String template() {
        return IOUtils.tryReadFile(TEMPLATE_PATH);
    }


    public static final class Result {
        PsiExpression constructorExpression;

        public Result(PsiExpression constructorExpression) {
            this.constructorExpression = constructorExpression;
        }

        public PsiExpression getConstructorExpression() {
            return constructorExpression;
        }
    }

    private class ExpressionMovedToConstructorListener extends PsiTreeChangeAdapter {
        private PsiExpression constructorExpression;

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            super.childAdded(event);

            PsiElement newlyAdded = event.getChild();
            Collection<PsiAssignmentExpression> possibleAssignments = getAssignmentExpressions(newlyAdded);
            for (PsiAssignmentExpression assignmentExpression : possibleAssignments) {
                processAssignmentExpression(assignmentExpression);
            }
        }

        private void processAssignmentExpression(PsiAssignmentExpression assignmentExpression) {
            PsiElement foundEquivalent = new PsiContainsChecker(assignmentExpression).findEquivalent(expression);
            if (foundEquivalent != null) {
                constructorExpression = (PsiExpression) foundEquivalent;
            }
        }

        private Collection<PsiAssignmentExpression> getAssignmentExpressions(PsiElement newlyAdded) {
            @SuppressWarnings("unchecked") Collection<PsiAssignmentExpression> result = PsiTreeUtil.collectElementsOfType(newlyAdded, PsiAssignmentExpression.class);
            return result;
        }

    }

}
