package com.simonstuck.vignelli.inspection.improvement.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.impl.TrainWreckExpressionRefactoringImpl;

import java.util.Collection;
import java.util.Collections;

public class TrainWreckExpressionImprovementOpportunity implements ImprovementOpportunity {
    private final TrainWreckIdentification trainWreckIdentification;

    public TrainWreckExpressionImprovementOpportunity(TrainWreckIdentification trainWreckIdentification) {
        this.trainWreckIdentification = trainWreckIdentification;
    }

    @Override
    public void beginRefactoring() {
        PsiElement trainWreckElement = trainWreckIdentification.getFinalCall();

        RefactoringTracker tracker = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        Project project = trainWreckElement.getProject();
        PsiFile file = trainWreckElement.getContainingFile();

        PsiStatement trainWreckStatement = PsiTreeUtil.getParentOfType(trainWreckElement, PsiStatement.class);
        Collection<PsiStatement> extractRegion = Collections.singletonList(trainWreckStatement);

        PsiElement criticalTrainWreckElement = null;
        if (TrainWreckExpressionRefactoringImpl.shouldCriticalCallRemain(trainWreckIdentification)) {
            criticalTrainWreckElement = trainWreckIdentification.criticalCall();
        }

        Refactoring refactoring = new TrainWreckExpressionRefactoringImpl(extractRegion, criticalTrainWreckElement, tracker, project, file, null);
        refactoring.begin();
    }
}
