package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProjectEval {
    private final String name;
    private final Map<String, ClassEval> classEvals = new HashMap<String, ClassEval>();

    public ProjectEval(String name) {
        this.name = name;
    }

    public void addClassEval(ClassEval classEval) {
        classEvals.put(classEval.getName(), classEval);
    }

    public void addAllClassEval(Collection<? extends ClassEval> classEvals) {
        for (ClassEval classEval : classEvals) {
            addClassEval(classEval);
        }
    }
}
