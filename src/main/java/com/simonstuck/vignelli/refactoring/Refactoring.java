package com.simonstuck.vignelli.refactoring;

import com.simonstuck.vignelli.Templatable;

import java.util.Map;

public interface Refactoring extends Templatable {

    /**
     * Checks if there are more steps remaining.
     * @return true iff no more steps are required.
     */
    boolean hasNextStep();

    /**
     * Performs the next step.
     * @throws NoSuchMethodException When no more steps are required.
     */
    void nextStep() throws NoSuchMethodException;

    /**
     * The number of total steps required in this refactoring.
     * @return The total steps required for this refactoring
     */
    int totalSteps();

    /**
     * The current step.
     * @return The current step number
     */
    int currentStepNumber();

    /**
     * Fills the given template value map with correct values that describe this refactoring to be shown in a UI.
     * @param templateValues The map of values to fill
     */
    void fillTemplateValues(Map<String, Object> templateValues);

    /**
     * Begins the refactoring.
     */
    void begin();

    /**
     * Completes the refactoring.
     */
    void complete();
}
