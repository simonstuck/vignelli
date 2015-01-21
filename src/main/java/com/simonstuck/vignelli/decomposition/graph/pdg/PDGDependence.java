package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.simonstuck.vignelli.decomposition.graph.GraphEdge;

public class PDGDependence extends GraphEdge<PDGNode> {
    private final PDGDependenceType type;

    /**
     * Creates a new PDG dependence.
     * @param src The source node
     * @param dst The destination node.
     * @param type The type of this dependence, e.g. CONTROL
     */
    public PDGDependence(PDGNode src, PDGNode dst, PDGDependenceType type) {
        super(src, dst);
        this.type = type;
    }

    /**
     * Gets the type of this dependence.
     * @return The dependence type.
     */
    public PDGDependenceType getType() {
        return type;
    }

    /**
     * Describes the type of a dependence.
     */
    public enum PDGDependenceType {
        CONTROL, DATA, ANTI, OUTPUT
    }
}
