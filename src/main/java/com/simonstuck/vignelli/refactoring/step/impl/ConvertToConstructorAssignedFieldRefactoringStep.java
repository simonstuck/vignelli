package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.introduceField.IntroduceFieldHandler;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;
import com.simonstuck.vignelli.psi.util.NavigationUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConvertToConstructorAssignedFieldRefactoringStep implements RefactoringStep {
    private static final String STEP_NAME = "Convert Expression to Constructor-Initialised Field";
    private static final String TEMPLATE_PATH = "descriptionTemplates/convertToConstructorAssignedFieldDescription.html";
    private final Project project;
    private final PsiExpression expression;
    private final RefactoringStepGoalChecker refactoringStepGoalChecker;
    private final Application application;

    @NotNull
    private final RefactoringStepDelegate delegate;

    public ConvertToConstructorAssignedFieldRefactoringStep(
            @NotNull PsiExpression expression,
            @NotNull Project project,
            @NotNull Application application,
            @NotNull RefactoringStepDelegate delegate
    ) {
        this.expression = expression;
        this.project = project;
        this.application = application;
        this.delegate = delegate;
        refactoringStepGoalChecker = new ExpressionMovedToConstructorChecker();
    }

    @Override
    public void start() {
        application.addApplicationListener(refactoringStepGoalChecker);
        NavigationUtil.navigateToElement(expression);
    }

    @Override
    public void end() {
        application.removeApplicationListener(refactoringStepGoalChecker);
    }

    @Override
    public void process() {
        PsiElement[] elements = new PsiElement[] { expression };

        IntroduceFieldHandler handler = new IntroduceFieldHandler();
        handler.invoke(project, elements, null);
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_NAME_TEMPLATE_KEY, STEP_NAME);
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtil.tryReadFile(TEMPLATE_PATH));
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


    /**
     * This checker checks whether the original expression has been moved to the constructor
     *
     * <p>This works as follows:</p>
     * <ol>
     *     <li>Record all assignments to fields in all constructors before we begin.</li>
     *     <li>
     *         <span>Then, for every change:</span>
     *         <ol>
     *             <li>Check for field assignments in the constructor that have been added since recording the originals</li>
     *             <li>For every new assignment expression, check if the original expression is within the RHS</li>
     *             <li>If so, check if the field is used in the code block that we originally removed it from.</li>
     *             <li>Check if the original expression is invalid</li>
     *         </ol>
     *     </li>
     * </ol>
     */
    private class ExpressionMovedToConstructorChecker extends RefactoringStepGoalChecker {

        private Set<PsiAssignmentExpression> originalConstructorFieldAssignments = new HashSet<PsiAssignmentExpression>();
        private final PsiClass clazz;
        private final PsiMethod originalExpressionMethod;

        public ExpressionMovedToConstructorChecker() {
            super(ConvertToConstructorAssignedFieldRefactoringStep.this, delegate);
            clazz = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            setUpOriginalConstructorAssignmentExpressions();
            originalExpressionMethod = PsiTreeUtil.getParentOfType(expression, PsiMethod.class);
        }

        private void setUpOriginalConstructorAssignmentExpressions() {
            originalConstructorFieldAssignments.addAll(getAllConstructorFieldAssignmentExpressions());
        }

        private Set<PsiAssignmentExpression> getAllConstructorFieldAssignmentExpressions() {
            Set<PsiAssignmentExpression> result = new HashSet<PsiAssignmentExpression>();
            if (clazz != null) {
                Set<PsiMethod> methods = getDefinedMethods(clazz);
                for (PsiMethod method : methods) {
                    result.addAll(getFieldAssignmentsIfConstructor(method));
                }
            }
            return result;
        }

        private Set<PsiAssignmentExpression> getFieldAssignmentsIfConstructor(PsiMethod method) {
            Set<PsiAssignmentExpression> result = new HashSet<PsiAssignmentExpression>();
            if (method.isValid() && method.isConstructor()) {
                @SuppressWarnings("unchecked")
                Collection<PsiAssignmentExpression> assignmentExpressions = PsiTreeUtil.collectElementsOfType(method, PsiAssignmentExpression.class);
                for (PsiAssignmentExpression assignmentExpression : assignmentExpressions) {
                    PsiExpression lExpression = assignmentExpression.getLExpression();
                    if (lExpression instanceof PsiReferenceExpression && ((PsiReferenceExpression) lExpression).resolve() instanceof PsiField) {
                        result.add(assignmentExpression);
                    }
                }
            }
            return result;
        }

        @Override
        public RefactoringStepResult computeResult() {
            Set<PsiAssignmentExpression> newAssignments = getAllConstructorFieldAssignmentExpressions();
            newAssignments.removeAll(originalConstructorFieldAssignments);

            for (PsiAssignmentExpression newAssignment : newAssignments) {
                PsiExpression constructorExpression = (PsiExpression) new PsiContainsChecker().findEquivalent(newAssignment, expression);
                PsiExpression lExpression = newAssignment.getLExpression();
                if (constructorExpression != null && lExpression instanceof PsiReferenceExpression) {
                    PsiElement lResolved = ((PsiReferenceExpression) lExpression).resolve();
                    if (lResolved instanceof PsiField) {
                        PsiField assignee = (PsiField) lResolved;

                        if (containsReferencesToField(originalExpressionMethod, assignee)) {
                            return new Result(constructorExpression);
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Checks whether the given method contains references to the given field.
         * @param method The method to check.
         * @param field The field for which to find references.
         * @return True iff the method contains references to the field.
         */
        private boolean containsReferencesToField(PsiMethod method, PsiField field) {
            @SuppressWarnings("unchecked")
            Collection<PsiReferenceExpression> referencesInOriginalMethod = PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);
            for (PsiReference referenceInOriginalMethod : referencesInOriginalMethod) {
                PsiElement resolvedReference = referenceInOriginalMethod.resolve();
                if (resolvedReference != null && resolvedReference.equals(field) && !expression.isValid()) {
                    return true;
                }
            }
            return false;
        }
    }
}
