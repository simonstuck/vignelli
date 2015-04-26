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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConvertToConstructorAssignedFieldRefactoringStep implements RefactoringStep {
    private static final String STEP_NAME = "Convert Expression to Constructor-Initialised Field";
    private static final String TEMPLATE_PATH = "descriptionTemplates/convertToConstructorAssignedFieldDescription.html";
    private final Project project;
    private final PsiExpression expression;
    private final ExpressionMovedToConstructorListener expressionMovedToConstructorListener;
    private final PsiManager psiManager;

    @Nullable
    private final RefactoringStepDelegate delegate;

    public ConvertToConstructorAssignedFieldRefactoringStep(
            @NotNull PsiExpression expression,
            @NotNull Project project,
            @NotNull PsiManager psiManager,
            @Nullable RefactoringStepDelegate delegate
    ) {
        this.expression = expression;
        this.project = project;
        this.psiManager = psiManager;
        this.delegate = delegate;
        expressionMovedToConstructorListener = new ExpressionMovedToConstructorListener();
    }

    @Override
    public void startListeningForGoal() {
        psiManager.addPsiTreeChangeListener(expressionMovedToConstructorListener);
    }

    @Override
    public void endListeningForGoal() {
        psiManager.removePsiTreeChangeListener(expressionMovedToConstructorListener);
    }

    @Override
    public Result process() {
        PsiElement[] elements = new PsiElement[] { expression };

        IntroduceFieldHandler handler = new IntroduceFieldHandler();
        handler.invoke(project, elements, null);

        return new Result(expressionMovedToConstructorListener.constructorExpression);
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, STEP_NAME);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtils.tryReadFile(TEMPLATE_PATH));
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        PsiClass thisClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
        if (thisClass != null) {
            contentMap.put("thisClass", thisClass.getName());
        }

        return template.render(contentMap);
    }


    public static final class Result implements RefactoringStepResult {
        PsiExpression constructorExpression;

        public Result(PsiExpression constructorExpression) {
            this.constructorExpression = constructorExpression;
        }

        public PsiExpression getConstructorExpression() {
            return constructorExpression;
        }

        @Override
        public boolean isSuccess() {
            return false;
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
                if (delegate != null) {
                    delegate.didFinishRefactoringStep(ConvertToConstructorAssignedFieldRefactoringStep.this, new Result(constructorExpression));
                }
            }
        }

        private Collection<PsiAssignmentExpression> getAssignmentExpressions(PsiElement newlyAdded) {
            @SuppressWarnings("unchecked") Collection<PsiAssignmentExpression> result = PsiTreeUtil.collectElementsOfType(newlyAdded, PsiAssignmentExpression.class);
            return result;
        }

    }

}
