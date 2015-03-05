package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;

public class RefactoringEngineComponentSeam extends RefactoringEngineComponent {
    boolean broadcastCalled;

    public RefactoringEngineComponentSeam(Project project) {
        super(project);
    }

    @Override
    protected void broadcastActiveRefactorings() {
        broadcastCalled = true;
    }
}
