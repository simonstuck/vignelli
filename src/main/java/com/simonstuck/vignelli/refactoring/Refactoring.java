package com.simonstuck.vignelli.refactoring;

import com.simonstuck.vignelli.Templatable;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public abstract class Refactoring extends Observable implements Templatable {

    /**
     * The key used for the boolean value of whether there is a next step in the template engine.
     */
    public static final String HAS_NEXT_STEP_TEMPLATE_KEY = "hasNextStep";

    /**
     * Checks if there are more step remaining.
     * @return true iff no more step are required.
     */
    public abstract boolean hasNextStep();

    /**
     * Performs the next step.
     * @throws NoSuchMethodException When no more step are required.
     */
    public abstract void nextStep();

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
