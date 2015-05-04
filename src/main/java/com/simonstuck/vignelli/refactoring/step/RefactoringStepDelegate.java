package com.simonstuck.vignelli.refactoring.step;

public interface RefactoringStepDelegate {
    void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result);
}
