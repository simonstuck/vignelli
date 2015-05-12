package com.simonstuck.vignelli.evaluation.datamodel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProjectStaticCallsSingletonClassifications {
    @NotNull
    private final String name;
    @NotNull
    private final Map<String, ClassSingletonUseClassification> classSingletonUseClassifications = new HashMap<String, ClassSingletonUseClassification>();

    public ProjectStaticCallsSingletonClassifications(@NotNull String name) {
        this.name = name;
    }

    public void addClassSingletonUseClassification(@NotNull ClassSingletonUseClassification classification) {
        classSingletonUseClassifications.put(classification.getName(), classification);
    }
}
