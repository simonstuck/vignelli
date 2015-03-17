package com.simonstuck.vignelli.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.TrainWreckVariableRefactoringImpl;

public class TrainWreckVariableImprovementOpportunity {

    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;

    public TrainWreckVariableImprovementOpportunity(PsiElement trainWreckElement, PsiLocalVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
    }

    public void beginRefactoring() {
        RefactoringTracker tracker = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        TrainWreckVariableRefactoringImpl refactoring = new TrainWreckVariableRefactoringImpl(trainWreckElement, variable, tracker);
        refactoring.begin();
    }
}
