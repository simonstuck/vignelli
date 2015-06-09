package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.LinkedList;
import java.util.List;

public class MethodPerformance {
    private final List<Long> trainWreckIdentificationSpeeds = new LinkedList<Long>();
    private final List<Long> directSingletonUseIdentificationSpeeds = new LinkedList<Long>();
    private final List<Long> complexMethodSpeeds = new LinkedList<Long>();

    public void addTrainWreckSpeed(long speed) {
        trainWreckIdentificationSpeeds.add(speed);
    }

    public void addSingletonSpeed(long speed) {
        directSingletonUseIdentificationSpeeds.add(speed);
    }

    public void addComplexMethodSpeed(long speed) {
        complexMethodSpeeds.add(speed);
    }
}
