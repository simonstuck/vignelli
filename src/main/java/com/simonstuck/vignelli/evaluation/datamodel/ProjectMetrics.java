package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashMap;
import java.util.Map;

public class ProjectMetrics {
    private final String name;
    private final Map<String, ClassMetrics> classMetrics = new HashMap<String, ClassMetrics>();

    public ProjectMetrics(String name) {
        this.name = name;
    }

    public void addClassMetrics(ClassMetrics classMetrics) {
        this.classMetrics.put(classMetrics.getName(), classMetrics);
    }

    public String getName() {
        return name;
    }

    public Map<String, ClassMetrics> getClassMetrics() {
        return new HashMap<String, ClassMetrics>(classMetrics);
    }
}
