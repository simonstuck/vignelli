package com.simonstuck.vignelli.evaluation.datamodel;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SingletonClassClassification {
    private final boolean isSingleton;
    private final Set<String> instanceRetrievalMembers = new HashSet<String>();

    public SingletonClassClassification(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }

    public void addInstanceRetrievalMember(@NotNull String instanceRetrievalMember) {
        instanceRetrievalMembers.add(instanceRetrievalMember);
    }

}
