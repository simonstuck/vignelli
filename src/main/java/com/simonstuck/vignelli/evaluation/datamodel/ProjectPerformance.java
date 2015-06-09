package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashMap;
import java.util.Map;

public class ProjectPerformance {
    private final String name;
    private final Map<String, ClassPerformance> classPerformance = new HashMap<String, ClassPerformance>();

    public ProjectPerformance(String name) {
        this.name = name;
    }

    public void addClassPerformance(ClassPerformance classMetrics) {
        this.classPerformance.put(classMetrics.getName(), classMetrics);
    }

    public String getName() {
        return name;
    }
}
