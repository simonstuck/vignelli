package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.refactoring.move.moveInstanceMethod.MoveInstanceMethodHandlerDelegate;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MoveMethodRefactoringStep implements RefactoringStep {

    private static final String MOVE_METHOD_STEP_NAME = "Move Method";

    private Project project;
    private PsiMethod methodToMove;

    public MoveMethodRefactoringStep(Project project, PsiMethod methodToMove) {
        this.project = project;
        this.methodToMove = methodToMove;
    }

    @Override
    public void startListeningForGoal() {

    }

    @Override
    public void endListeningForGoal() {

    }

    @Override
    public Result process() {
        MoveInstanceMethodHandlerDelegate moveInstanceMethodHandlerDelegate = new MoveInstanceMethodHandlerDelegate();
        final PsiElement[] elements = new PsiElement[] {methodToMove};
        moveInstanceMethodHandlerDelegate.doMove(project, elements, null, null);

        return null;
    }

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, MOVE_METHOD_STEP_NAME);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("method", methodToMove.getText());

        PsiExpression targetExpression = getTargetExpression(methodToMove);
        if (targetExpression != null) {
            contentMap.put("targetVariable", targetExpression.getText());
            targetExpression.getType();
            PsiClass clazz = PsiTypesUtil.getPsiClass(targetExpression.getType());
            if (clazz != null) {
                contentMap.put("targetClass", clazz.getName());
            }
        }

        return template.render(contentMap);
    }

    public PsiExpression getFinalQualifier(PsiExpression element) {
        if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression expression = (PsiMethodCallExpression) element;
            PsiReferenceExpression methodRefExpression = expression.getMethodExpression();
            return getFinalQualifier(methodRefExpression.getQualifierExpression());
        } else {
            return element;
        }
    }

    private String template() {
        return IOUtils.tryReadFile("descriptionTemplates/moveMethodStepDescription.html");
    }

    @Nullable
    private PsiExpression getTargetExpression(PsiMethod methodToMove) {
        MethodChainIdentificationEngine engine = new MethodChainIdentificationEngine();
        Set<MethodChainIdentification> methodChainIdentifications = engine.identifyMethodChains(methodToMove);
        if (!methodChainIdentifications.isEmpty()) {
            MethodChainIdentification first = methodChainIdentifications.iterator().next();
            return getFinalQualifier(first.getFinalCall());
        } else {
            return null;
        }
    }


    private static class MethodMovedToTargetGoalChecker extends RefactoringStepGoalChecker {

        public MethodMovedToTargetGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);
        }

        @Override
        public RefactoringStepResult computeResult() {
            return null;
        }
    }

    public static final class Result implements RefactoringStepResult {
        private final PsiMethod newMethod;

        public Result(PsiMethod newMethod) {
            this.newMethod = newMethod;
        }

        public PsiMethod getNewMethod() {
            return newMethod;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }
}
