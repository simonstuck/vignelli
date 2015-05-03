package com.simonstuck.vignelli.evaluation.datamodel;

import java.util.HashSet;
import java.util.Set;

public class ClassEval {
    private final String name;
    private final Set<MethodEval> methodEvals = new HashSet<MethodEval>();

    public ClassEval(String name) {
        this.name = name;
    }

    public void addMethodEval(MethodEval methodEval) {
        methodEvals.add(methodEval);
    }

    public String getName() {
        return name;
    }
}
