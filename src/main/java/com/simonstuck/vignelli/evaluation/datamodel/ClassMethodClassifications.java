package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashMap;
import java.util.Map;

public class ClassMethodClassifications {
    private final String name;
    private final Map<String, MethodClassification> methodClassifications = new HashMap<String, MethodClassification>();

    public ClassMethodClassifications(String name) {
        this.name = name;
    }

    public void addMethodClassification(String name, MethodClassification evaluation) {
        methodClassifications.put(name, evaluation);
    }

    public String getName() {
        return name;
    }
}
