package com.simonstuck.vignelli.refactoring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.intellij.openapi.project.Project;

import org.junit.Before;
import org.junit.Test;

public class RefactoringEngineComponentTest {

    private Project project;
    private RefactoringEngineComponentSeam engine;

    @Before
    public void setUp() throws Exception {
        project = mock(Project.class);
        engine = new RefactoringEngineComponentSeam(project);
    }

    @Test
    public void shouldNotifyObserversIfNewRefactoringIsAdded() throws Exception {
        engine.runStep(mock(Refactoring.class));
        assertTrue(engine.broadcastCalled);
    }

    @Test
    public void shouldNotNotifyObserversIfRefactoringStepIsExecutedOnExistingRefactoring() throws Exception {
        Refactoring refactoring = mock(Refactoring.class);
        engine.runStep(refactoring);
        engine.broadcastCalled = false;
        engine.runStep(refactoring);
        assertFalse(engine.broadcastCalled);
    }

    @Test
    public void shouldRunOneStepOfARefactoringWhenAsked() throws Exception {
        Refactoring refactoring = mock(Refactoring.class);
        engine.runStep(refactoring);
        verify(refactoring, times(1)).nextStep();
    }
}