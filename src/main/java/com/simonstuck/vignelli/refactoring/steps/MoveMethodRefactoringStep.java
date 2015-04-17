package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.move.MoveCallback;
import com.intellij.refactoring.move.moveInstanceMethod.MoveInstanceMethodHandlerDelegate;
import com.simonstuck.vignelli.inspection.MethodChainingInspectionTool;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MoveMethodRefactoringStep {

    private static final Logger LOG = Logger.getInstance(MoveMethodRefactoringStep.class.getName());
    private static final String MOVE_METHOD_STEP_NAME = "Move Method";

    private Project project;
    private PsiMethod methodToMove;

    public MoveMethodRefactoringStep(Project project, PsiMethod methodToMove) {
        this.project = project;
        this.methodToMove = methodToMove;
    }

    public Result process() {
        MoveInstanceMethodHandlerDelegate delegate = new MoveInstanceMethodHandlerDelegate();
        final PsiMethod targetMethod = performMoveRefactoring(delegate, methodToMove);

        return new Result(targetMethod);
    }

    private PsiMethod performMoveRefactoring(MoveInstanceMethodHandlerDelegate delegate, PsiMethod method) {
        final PsiElement[] elements = new PsiElement[] { method };
        final PsiMethod[] targetMethod = { null };

        PsiTreeChangeListener listener = new MoveTargetListener(targetMethod);
        PsiManager manager = PsiManager.getInstance(project);

        manager.addPsiTreeChangeListener(listener);
        delegate.doMove(project, elements, null, new MoveCallback() {
            @Override
            public void refactoringCompleted() {
                LOG.debug("refactoring complete");
            }
        });
        manager.removePsiTreeChangeListener(listener);

        return targetMethod[0];
    }

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", MOVE_METHOD_STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("method", methodToMove.getText());

        PsiElement targetExpression = getTargetExpression(methodToMove);
        if (targetExpression != null) {
            contentMap.put("targetVariable", targetExpression.getText());
            PsiClass clazz = PsiTreeUtil.getParentOfType(targetExpression, PsiClass.class);
            if (clazz != null) {
                contentMap.put("targetClass", clazz.getName());
            }
        }

        return template.render(contentMap);
    }

    private boolean isVariableReference(PsiElement finalElement) {
        return finalElement instanceof PsiReferenceExpression && ((PsiReferenceExpression) finalElement).resolve() instanceof PsiVariable;
    }

    public PsiElement getFinalQualifier(PsiElement element) {
        if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression expression = (PsiMethodCallExpression) element;
            PsiReferenceExpression methodRefExpression = expression.getMethodExpression();
            return getFinalQualifier(methodRefExpression.getQualifierExpression());
        } else {
            return element;
        }
    }

    private String template() {
        try {
            return IOUtils.readFile("descriptionTemplates/moveMethodStepDescription.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Nullable
    private PsiElement getTargetExpression(PsiMethod methodToMove) {
        MethodChainingInspectionTool tool = new MethodChainingInspectionTool();

        InspectionManager manager = new InspectionManagerEx(project);
        ProblemDescriptor[] descriptors = tool.checkMethod(methodToMove, manager, false);
        if (descriptors != null && descriptors.length > 0) {
            ProblemDescriptor first = descriptors[0];
            PsiElement finalElement = getFinalQualifier(first.getPsiElement());
            if (isVariableReference(finalElement)) {
                return finalElement;
            }
        }
        return null;
    }

    private static class MoveTargetListener extends PsiTreeChangeAdapter {
        private final PsiMethod[] targetMethod;

        public MoveTargetListener(PsiMethod[] targetMethod) {
            this.targetMethod = targetMethod;
        }

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            super.childAdded(event);

            if (event.getChild() instanceof PsiMethod) {
                targetMethod[0] = (PsiMethod) event.getChild();
            }
        }
    }

    public class Result {
        private final PsiMethod newMethod;

        public Result(PsiMethod newMethod) {
            this.newMethod = newMethod;
        }

        public PsiMethod getNewMethod() {
            return newMethod;
        }
    }
}
