package com.simonstuck.vignelli.decomposition.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class GraphNode<T extends GraphNode> {
    private final int id;
    private final Set<GraphEdge<T>> incomingEdges;
    private final Set<GraphEdge<T>> outgoingEdges;

    /**
     * Constructs a new graph node with a given unique id.
     * @param id The id to use for this graph node.
     */
    public GraphNode(int id) {
        this.id = id;
        this.incomingEdges = new LinkedHashSet<GraphEdge<T>>();
        this.outgoingEdges = new LinkedHashSet<GraphEdge<T>>();
    }

    public int getId() {
        return id;
    }

    public void addIncomingEdge(GraphEdge<T> edge) {
        incomingEdges.add(edge);
    }

    public void addOutgoingEdge(GraphEdge<T> edge) {
        outgoingEdges.add(edge);
    }

    public Set<GraphEdge<T>> getIncomingEdges() {
        return incomingEdges;
    }

    public Set<GraphEdge<T>> getOutgoingEdges() {
        return outgoingEdges;
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
