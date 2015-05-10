package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassMetrics {
    private final String name;
    private final Map<String, MethodMetrics> methodMetrics = new HashMap<String, MethodMetrics>();

    public ClassMetrics(String name) {
        this.name = name;
    }

    public void addMethodMetrics(String qualifiedName, MethodMetrics metrics) {
        methodMetrics.put(qualifiedName, metrics);
    }

    public String getName() {
        return name;
    }
}
