package com.simonstuck.vignelli.inspection.improvement.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.psi.util.PsiElementUtil;
import com.simonstuck.vignelli.refactoring.impl.DirectSingletonUseRefactoringImpl;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;

public class DirectSingletonUseImprovementOpportunity implements ImprovementOpportunity {

    private PsiMethodCallExpression getInstanceElement;

    public DirectSingletonUseImprovementOpportunity(PsiMethodCallExpression getInstanceElement) {
        this.getInstanceElement = getInstanceElement;
    }

    @Override
    public void beginRefactoring() {
        if (PsiElementUtil.isAnyNullOrInvalid(getInstanceElement)) {
            return;
        }
        Project project = getInstanceElement.getProject();
        RefactoringTracker tracker = project.getComponent(RefactoringEngineComponent.class);
        PsiFile file = getInstanceElement.getContainingFile();

        Refactoring refactoring = new DirectSingletonUseRefactoringImpl(getInstanceElement, tracker, project, file);
        refactoring.begin();
    }
}
