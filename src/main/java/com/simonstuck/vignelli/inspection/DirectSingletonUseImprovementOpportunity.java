package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.refactoring.DirectSingletonUseRefactoringImpl;
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
        Project project = getInstanceElement.getProject();
        RefactoringTracker tracker = project.getComponent(RefactoringEngineComponent.class);
        PsiFile file = getInstanceElement.getContainingFile();

        Refactoring refactoring = new DirectSingletonUseRefactoringImpl(getInstanceElement, tracker, project, file);
        refactoring.begin();
    }
}
