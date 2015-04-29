package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.psi.FileViewProvider;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a goal checker that can be registered as a tree change visitor.
 *
 * <p>Goal checkers find a pattern in the code as it currently stands and once they have found it
 * report a result to a delegate.</p>
 */
public abstract class RefactoringStepGoalChecker extends PsiTreeChangeAdapter {

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
     * Checks if any of the files is currently in an invalid state.
     *
     * <p>Note that this method is necessary because there appears to be an IntelliJ bug that causes files to have two contents at once.</p>
     * @see {https://devnet.jetbrains.com/message/5541319}
     *
     * @param files The files to check
     * @return true iff any of the files are invalid, false otherwise.
     */
    protected boolean isAnyOfTheseFilesInvalidHack(PsiFile... files) {
        for (PsiFile file : files) {
            FileViewProvider viewProvider = file.getViewProvider();
            CharSequence contents = viewProvider.getContents();
            if (contents.length() != file.getTextLength()) {
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
        final Set<PsiMethod> methods = new HashSet<PsiMethod>();
        JavaRecursiveElementVisitor methodFinder = new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                methods.add(method);
            }
        };
        clazz.accept(methodFinder);
        return methods;
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        super.childrenChanged(event);
        performCheck();
    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        super.childAdded(event);
        performCheck();
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        super.childReplaced(event);
        performCheck();
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        super.childRemoved(event);
        performCheck();
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        super.childMoved(event);
        performCheck();
    }
}
