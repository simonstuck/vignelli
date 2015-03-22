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
import com.intellij.refactoring.move.moveInstanceMethod.MoveInstanceMethodHandlerDelegate;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MoveMethodRefactoringStep implements RefactoringStep {

    private static final Logger LOG = Logger.getInstance(MoveMethodRefactoringStep.class.getName());

    private Project project;
    private PsiMethod methodToMove;

    public MoveMethodRefactoringStep(Project project, PsiMethod methodToMove) {
        this.project = project;
        this.methodToMove = methodToMove;
    }

    @Override
    public Map<String, Object> process() {
        MoveInstanceMethodHandlerDelegate delegate = new MoveInstanceMethodHandlerDelegate();
        PsiElement[] elements = new PsiElement[] { methodToMove };

        final PsiMethod[] targetMethod = { null };

        PsiTreeChangeListener listener = new PsiTreeChangeAdapter() {
            @Override
            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                super.childAdded(event);

                if (event.getChild() instanceof PsiMethod) {
                    targetMethod[0] = (PsiMethod) event.getChild();
                }
            }
        };

        PsiManager manager = PsiManager.getInstance(project);
        manager.addPsiTreeChangeListener(listener);
        delegate.doMove(project, elements, null, () -> LOG.info("refactoring complete"));
        manager.removePsiTreeChangeListener(listener);

        PsiClass targetClass = PsiTreeUtil.getParentOfType(targetMethod[0], PsiClass.class);

        Map<String, Object> results = new HashMap<>();
        results.put("targetMethod", targetMethod[0]);
        results.put("targetClass", targetClass);

        return results;
    }
}
