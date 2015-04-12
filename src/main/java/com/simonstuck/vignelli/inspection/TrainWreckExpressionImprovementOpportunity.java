package com.simonstuck.vignelli.inspection;

import com.intellij.psi.PsiElement;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.TrainWreckExpressionRefactoringImpl;

public class TrainWreckExpressionImprovementOpportunity implements ImprovementOpportunity {
    private final PsiElement trainWreckElement;

    public TrainWreckExpressionImprovementOpportunity(PsiElement trainWreckElement) {
        this.trainWreckElement = trainWreckElement;
    }

    @Override
    public void beginRefactoring() {
        RefactoringTracker tracker = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        Refactoring refactoring = new TrainWreckExpressionRefactoringImpl(trainWreckElement, tracker);
        refactoring.begin();
    }
}
