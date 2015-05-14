package com.simonstuck.vignelli.refactoring.step;

public interface RefactoringStepDelegate {

    /**
     * Called by the given {@link com.simonstuck.vignelli.refactoring.step.RefactoringStep} when it is done.
     * <p>Note that this method can be called on multiple threads.</p>
     * @param step The refactoring step that called this method.
     * @param result The result that the refactoring step arrived at.
     */
    void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result);
}
