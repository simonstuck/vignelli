package com.simonstuck.vignelli.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiVariable;

public class TrainWreckVariableImprovementOpportunity {

    private final PsiElement trainWreckElement;
    private final PsiVariable variable;

    public TrainWreckVariableImprovementOpportunity(PsiElement trainWreckElement, PsiVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
    }
}
