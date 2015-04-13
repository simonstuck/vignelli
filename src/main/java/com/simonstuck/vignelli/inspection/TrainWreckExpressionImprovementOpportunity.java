package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.TrainWreckExpressionRefactoringImpl;

import java.util.Collection;
import java.util.Collections;

public class TrainWreckExpressionImprovementOpportunity implements ImprovementOpportunity {
    private final PsiElement trainWreckElement;

    public TrainWreckExpressionImprovementOpportunity(PsiElement trainWreckElement) {
        this.trainWreckElement = trainWreckElement;
    }

    @Override
    public void beginRefactoring() {
        RefactoringTracker tracker = trainWreckElement.getProject().getComponent(RefactoringEngineComponent.class);
        Project project = trainWreckElement.getProject();
        PsiFile file = trainWreckElement.getContainingFile();

        PsiStatement trainWreckStatement = PsiTreeUtil.getParentOfType(trainWreckElement, PsiStatement.class);
        Collection<PsiStatement> extractRegion = Collections.singletonList(trainWreckStatement);

        Refactoring refactoring = new TrainWreckExpressionRefactoringImpl(extractRegion, tracker, project, file);
        refactoring.begin();
    }
}
