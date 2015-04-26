package com.simonstuck.vignelli.refactoring.steps;

import java.util.Map;

public interface RefactoringStep {
    void startListeningForGoal();
    void endListeningForGoal();
    RefactoringStepResult process();

    void describeStep(Map<String, Object> templateValues);
}
