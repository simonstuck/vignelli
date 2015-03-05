package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.refactoring.TrainWreckVariableRefactoringImpl;

public class TrainWreckVariableImprovementOpportunity {

    private static final Logger LOG = Logger.getInstance(TrainWreckVariableImprovementOpportunity.class.getName());

    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;
    private TrainWreckVariableRefactoringImpl refactoring;

    public TrainWreckVariableImprovementOpportunity(PsiElement trainWreckElement, PsiLocalVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
    }

    public void beginRefactoring() {
        //FIXME: What if the refactoring is already started?
        refactoring = new TrainWreckVariableRefactoringImpl(trainWreckElement, variable);
        try {
            refactoring.nextStep();
        } catch (NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
