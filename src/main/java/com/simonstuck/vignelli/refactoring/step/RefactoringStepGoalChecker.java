package com.simonstuck.vignelli.refactoring.step;

import com.intellij.openapi.application.ApplicationListener;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a goal checker that can be registered as a tree change visitor.
 *
 * <p>Goal checkers find a pattern in the code as it currently stands and once they have found it
 * report a result to a delegate.</p>
 */
public abstract class RefactoringStepGoalChecker implements ApplicationListener {

    private boolean notified = false;
    @NotNull
    private final RefactoringStep refactoringStep;
    @NotNull
    private final RefactoringStepDelegate delegate;

    public RefactoringStepGoalChecker(@NotNull RefactoringStep refactoringStep, @NotNull RefactoringStepDelegate delegate) {
        this.refactoringStep = refactoringStep;
        this.delegate = delegate;
    }

    /**
     * Computes the refactoring step result for the current state.
     * <p>If the current state is not the goal state, this method should return null.</p>
     * @return result instance if the goal has been reached (successful) or cannot be reached (unsuccessful), otherwise null if no conclusion has been reached.
     */
    public abstract RefactoringStepResult computeResult();

    /**
     * Performs the check at the current state of the AST.
     */
    private synchronized void performCheck() {
        RefactoringStepResult result = computeResult();
        if (result != null && !notified) {
            notifyDelegateIfNecessary(result);
            notified = true;
        }
    }

    /**
     * Notifies the delegate if possible. This method is only ever called at most once.
     */
    private void notifyDelegateIfNecessary(RefactoringStepResult result) {
        delegate.didFinishRefactoringStep(refactoringStep, result);
    }


    /**
     * Checks if any of the given {@link com.intellij.psi.PsiElement}s are null or invalid.
     * @param elements The elements to check.
     * @return True iff any fo the elements are null or invalid.
     */
    protected boolean isAnyNullOrInvalid(PsiElement... elements) {
        for (PsiElement element : elements) {
            if (element == null || !element.isValid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method that retrieves all defined methods in the given class.
     * @param clazz The class for which to compute methods.
     * @return The set of defined methods in the given class.
     */
    protected Set<PsiMethod> getDefinedMethods(PsiClass clazz) {
        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = PsiTreeUtil.collectElementsOfType(clazz, PsiMethod.class);
        return new HashSet<PsiMethod>(methods);
    }

    @Override
    public void writeActionFinished(Object action) {
        performCheck();
    }

    @Override
    public boolean canExitApplication() {
        return false;
    }

    @Override
    public void applicationExiting() {

    }

    @Override
    public void beforeWriteActionStart(Object action) {

    }

    @Override
    public void writeActionStarted(Object action) {

    }
}
