package com.simonstuck.vignelli.refactoring;

import com.simonstuck.vignelli.Templatable;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public abstract class Refactoring extends Observable implements Templatable {

    /**
     * Checks if there are more steps remaining.
     * @return true iff no more steps are required.
     */
    public abstract boolean hasNextStep();

    /**
     * Performs the next step.
     * @throws NoSuchMethodException When no more steps are required.
     */
    public abstract void nextStep() throws NoSuchMethodException;

    /**
     * Fills the given template value map with correct values that describe this refactoring to be shown in a UI.
     * @param templateValues The map of values to fill
     */
    public abstract void fillTemplateValues(Map<String, Object> templateValues);

    /**
     * Begins the refactoring.
     */
    public abstract void begin();

    /**
     * Completes the refactoring.
     */
    public abstract void complete();
}
