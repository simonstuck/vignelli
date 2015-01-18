package com.simonstuck.vignelli.decomposition.graph;

import com.simonstuck.vignelli.utils.IdGenerator;

public class GraphNodeIdGenerator implements IdGenerator<Integer> {
    private int id = 0;

    @Override
    public Integer generateId() {
        int oldId = id;
        id++;
        return oldId;
    }
}
