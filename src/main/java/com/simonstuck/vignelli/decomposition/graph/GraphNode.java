package com.simonstuck.vignelli.decomposition.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class GraphNode {
    private final int id;
    private final Set<GraphEdge> incomingEdges;
    private final Set<GraphEdge> outgoingEdges;

    /**
     * Constructs a new graph node with a given unique id.
     * @param id The id to use for this graph node.
     */
    public GraphNode(int id) {
        this.id = id;
        this.incomingEdges = new LinkedHashSet<GraphEdge>();
        this.outgoingEdges = new LinkedHashSet<GraphEdge>();
    }

    public int getId() {
        return id;
    }

    public void addIncomingEdge(GraphEdge edge) {
        incomingEdges.add(edge);
    }

    public void addOutgoingEdge(GraphEdge edge) {
        outgoingEdges.add(edge);
    }

    public Set<GraphEdge> getIncomingEdges() {
        return incomingEdges;
    }

    public Set<GraphEdge> getOutgoingEdges() {
        return outgoingEdges;
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
