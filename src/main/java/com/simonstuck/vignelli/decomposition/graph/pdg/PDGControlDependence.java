package com.simonstuck.vignelli.decomposition.graph.pdg;

public class PDGControlDependence extends PDGDependence {
    public PDGControlDependence(PDGNode src, PDGNode dst) {
        super(src, dst, PDGDependenceType.CONTROL);
    }
}
