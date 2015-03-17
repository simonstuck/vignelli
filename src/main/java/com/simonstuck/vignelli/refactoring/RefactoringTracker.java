package com.simonstuck.vignelli.refactoring;

import java.util.Collection;

public interface RefactoringTracker {
    /**
     * Adds the given refactoring to the active ones.
     *
     * @param refactoring The refactoring to add
     */
    void add(Refactoring refactoring);

    /**
     * Removes the given refactoring from the tracker.
     *
     * @param refactoring The refactoring to remove
     */
    void remove(Refactoring refactoring);

    /**
     * Gets all active refactorings.
     *
     * @return A new set of all active refactorings at this point.
     */
    public Collection<Refactoring> activeRefactorings();
}