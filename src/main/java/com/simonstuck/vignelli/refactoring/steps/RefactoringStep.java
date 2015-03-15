package com.simonstuck.vignelli.refactoring.steps;

import java.util.Map;

public interface RefactoringStep {
    Map<String, Object> process();
}
