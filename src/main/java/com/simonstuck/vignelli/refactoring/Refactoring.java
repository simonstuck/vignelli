package com.simonstuck.vignelli.refactoring;

import com.simonstuck.vignelli.Templatable;

import java.util.Map;

public interface Refactoring extends Templatable {

    boolean hasNextStep();

    void nextStep() throws NoSuchMethodException;

    int totalSteps();

    int currentStepNumber();

    void fillTemplateValues(Map<String, Object> templateValues);
}
