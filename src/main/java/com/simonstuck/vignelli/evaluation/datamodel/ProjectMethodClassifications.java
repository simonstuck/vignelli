package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashMap;
import java.util.Map;

public class ProjectMethodClassifications {
    private final String name;
    private final Map<String, ClassMethodClassifications> classMethodClassificationsMap = new HashMap<String, ClassMethodClassifications>();

    public ProjectMethodClassifications(String name) {
        this.name = name;
    }

    public void addClassMethodClassification(ClassMethodClassifications evaluation) {
        classMethodClassificationsMap.put(evaluation.getName(), evaluation);
    }
}
