package com.simonstuck.vignelli.inspection.improvement.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.impl.TrainWreckVariableRefactoringImpl;

public class TrainWreckVariableImprovementOpportunity implements ImprovementOpportunity {

    private final TrainWreckIdentification trainWreckIdentification;
    private final PsiLocalVariable variable;

    public TrainWreckVariableImprovementOpportunity(TrainWreckIdentification trainWreckIdentification, PsiLocalVariable variable) {
        this.trainWreckIdentification = trainWreckIdentification;
        this.variable = variable;
    }

    @Override
    public void beginRefactoring() {
        PsiElement trainWreckElement = trainWreckIdentification.getFinalCall();
        RefactoringTracker tracker = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        TrainWreckVariableRefactoringImpl refactoring = new TrainWreckVariableRefactoringImpl(trainWreckElement, variable, tracker);
        refactoring.begin();
    }
}
