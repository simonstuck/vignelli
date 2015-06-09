package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashMap;
import java.util.Map;

public class ClassPerformance {
    private final String name;
    private final Map<String, MethodPerformance> methodPerformance = new HashMap<String, MethodPerformance>();

    public ClassPerformance(String name) {
        this.name = name;
    }

    public void addMethodPerformance(String qualifiedName, MethodPerformance performance) {
        methodPerformance.put(qualifiedName, performance);
    }

    public String getName() {
        return name;
    }
}
