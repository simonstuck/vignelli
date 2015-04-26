package com.simonstuck.vignelli.refactoring.steps;

public interface RefactoringStepDelegate {
    void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result);
}
