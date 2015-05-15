package com.simonstuck.vignelli.inspection.improvement.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.impl.InternalGetterUseRefactoringImpl;

import org.jetbrains.annotations.NotNull;

public class InternalGetterUseImprovementOpportunity implements ImprovementOpportunity {
    @NotNull
    private final PsiMethodCallExpression getterCall;

    public InternalGetterUseImprovementOpportunity(@NotNull PsiMethodCallExpression getterCall) {
        this.getterCall = getterCall;
    }

    @Override
    public void beginRefactoring() {
        Project project = getterCall.getProject();
        RefactoringTracker tracker = project.getComponent(RefactoringEngineComponent.class);
        Refactoring refactoring = new InternalGetterUseRefactoringImpl(getterCall, tracker);
        refactoring.begin();
    }

}
