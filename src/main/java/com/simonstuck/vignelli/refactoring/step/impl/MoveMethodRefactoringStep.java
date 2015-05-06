package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.refactoring.move.moveInstanceMethod.MoveInstanceMethodHandlerDelegate;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.psi.ClassFinder;
import com.simonstuck.vignelli.psi.IntelliJClassFinder;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;
import com.simonstuck.vignelli.psi.util.EditorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MoveMethodRefactoringStep implements RefactoringStep {

    private static final String MOVE_METHOD_STEP_NAME = "Move Method";

    private Project project;
    private PsiMethod methodToMove;
    private final PsiExpression targetExpression;
    private final PsiClass targetClass;
    private final MethodMovedToTargetGoalChecker methodMovedToTargetGoalChecker;
    private final Application application;

    public MoveMethodRefactoringStep(Project project, PsiMethod methodToMove, Application application, RefactoringStepDelegate delegate) {
        this.project = project;
        this.methodToMove = methodToMove;
        this.application = application;

        targetExpression = getTargetExpression(methodToMove);
        if (targetExpression != null) {
            targetClass = PsiTypesUtil.getPsiClass(targetExpression.getType());
        } else {
            targetClass = null;
        }

        methodMovedToTargetGoalChecker = new MethodMovedToTargetGoalChecker(this, delegate);
    }

    @Override
    public void start() {
        application.addApplicationListener(methodMovedToTargetGoalChecker);
        EditorUtil.navigateToElement(methodToMove);
    }

    @Override
    public void end() {
        application.removeApplicationListener(methodMovedToTargetGoalChecker);
    }

    @Override
    public void process() {
        MoveInstanceMethodHandlerDelegate moveInstanceMethodHandlerDelegate = new MoveInstanceMethodHandlerDelegate();
        final PsiElement[] elements = new PsiElement[] {methodToMove};
        moveInstanceMethodHandlerDelegate.doMove(project, elements, null, null);
    }

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, MOVE_METHOD_STEP_NAME);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("method", methodToMove.getText());

        if (targetExpression != null) {
            contentMap.put("targetVariable", targetExpression.getText());
        }
        if (targetClass != null) {
            contentMap.put("targetClass", targetClass.getName());
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
        return IOUtil.tryReadFile("descriptionTemplates/moveMethodStepDescription.html");
    }

    @Nullable
    private PsiExpression getTargetExpression(PsiMethod methodToMove) {
        MethodChainIdentificationEngine engine = new MethodChainIdentificationEngine(new IntelliJClassFinder(project));
        Set<MethodChainIdentification> methodChainIdentifications = engine.identifyMethodChains(methodToMove);
        if (!methodChainIdentifications.isEmpty()) {
            MethodChainIdentification first = methodChainIdentifications.iterator().next();
            return getFinalQualifier(first.getFinalCall());
        } else {
            return null;
        }
    }


    /**
     * This checker checks whether the method to move has been moved onto the target class in question.
     *
     * <p>This is done in the following way:</p>
     * <ol>
     *     <li>Initially all methods of the target class are saved</li>
     *     <li>
     *         <span>For every change:</span>
     *         <ol>
     *             <li>Find new methods added to target class (using original methods from before)</li>
     *             <li>If any of the new methods' bodies is similar to the original one (contains it) then the method has been moved.</li>
     *         </ol>
     *     </li>
     * </ol>
     */
    private class MethodMovedToTargetGoalChecker extends RefactoringStepGoalChecker {

        private final Set<PsiMethod> originalMethodsInTargetClass;

        public MethodMovedToTargetGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
            super(refactoringStep, delegate);

            originalMethodsInTargetClass = getDefinedMethods(targetClass);
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (isAnyNullOrInvalid(targetClass)) {
                return new Result(false, null);
            }

            Set<PsiMethod> newMethodsInTargetClass = getDefinedMethods(targetClass);
            newMethodsInTargetClass.removeAll(originalMethodsInTargetClass);

            for (PsiMethod newMethod : newMethodsInTargetClass) {
                if (new PsiContainsChecker().findEquivalent(newMethod.getBody(),methodToMove.getBody()) != null) {
                    return new Result(true, newMethod);
                }
            }

            return null;
        }
    }

    public static final class Result implements RefactoringStepResult {
        private final boolean success;
        private final PsiMethod newMethod;

        public Result(boolean success, PsiMethod newMethod) {
            this.success = success;
            this.newMethod = newMethod;
        }

        public PsiMethod getNewMethod() {
            return newMethod;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
    }
}
