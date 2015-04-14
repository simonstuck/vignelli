package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.move.MoveCallback;
import com.intellij.refactoring.move.moveInstanceMethod.MoveInstanceMethodHandlerDelegate;

import org.jetbrains.annotations.NotNull;

public class MoveMethodRefactoringStep {

    private static final Logger LOG = Logger.getInstance(MoveMethodRefactoringStep.class.getName());

    private Project project;
    private PsiMethod methodToMove;

    public MoveMethodRefactoringStep(Project project, PsiMethod methodToMove) {
        this.project = project;
        this.methodToMove = methodToMove;
    }

    public Result process() {
        MoveInstanceMethodHandlerDelegate delegate = new MoveInstanceMethodHandlerDelegate();
        final PsiMethod targetMethod = performMoveRefactoring(delegate, methodToMove);
        PsiClass targetClass = getClassForMethod(targetMethod);

        return new Result(targetClass, targetMethod);
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

    private PsiClass getClassForMethod(PsiMethod targetMethod) {
        return PsiTreeUtil.getParentOfType(targetMethod, PsiClass.class);
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
        private final PsiClass newClass;
        private final PsiMethod newMethod;

        public Result(PsiClass newClass, PsiMethod newMethod) {
            this.newClass = newClass;
            this.newMethod = newMethod;
        }

        public PsiClass getNewClass() {
            return newClass;
        }

        public PsiMethod getNewMethod() {
            return newMethod;
        }
    }
}
