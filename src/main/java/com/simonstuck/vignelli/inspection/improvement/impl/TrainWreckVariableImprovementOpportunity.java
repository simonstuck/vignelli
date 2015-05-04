package com.simonstuck.vignelli.inspection.improvement.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.impl.TrainWreckVariableRefactoringImpl;

public class TrainWreckVariableImprovementOpportunity implements ImprovementOpportunity {

    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;

    public TrainWreckVariableImprovementOpportunity(PsiElement trainWreckElement, PsiLocalVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
    }

    @Override
    public void beginRefactoring() {
        RefactoringTracker tracker = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        TrainWreckVariableRefactoringImpl refactoring = new TrainWreckVariableRefactoringImpl(trainWreckElement, variable, tracker);
        refactoring.begin();
    }
}
