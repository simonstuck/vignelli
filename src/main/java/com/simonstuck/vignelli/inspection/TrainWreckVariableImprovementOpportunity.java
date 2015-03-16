package com.simonstuck.vignelli.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.TrainWreckVariableRefactoringImpl;

public class TrainWreckVariableImprovementOpportunity {

    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;

    public TrainWreckVariableImprovementOpportunity(PsiElement trainWreckElement, PsiLocalVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
    }

    public void beginRefactoring() {
        TrainWreckVariableRefactoringImpl refactoring = new TrainWreckVariableRefactoringImpl(trainWreckElement, variable);
        RefactoringEngineComponent refactoringEngine = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        refactoringEngine.add(refactoring);
    }
}
