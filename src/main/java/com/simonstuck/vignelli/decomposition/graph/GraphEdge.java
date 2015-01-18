package com.simonstuck.vignelli.decomposition.graph;

public class GraphEdge {
    private final GraphNode src;
    private final GraphNode dst;

    public GraphEdge(GraphNode src, GraphNode dst) {
        this.src = src;
        this.dst = dst;
    }

    public GraphNode getSource() {
        return src;
    }

    public GraphNode getDestination() {
        return dst;
    }

    @Override
    public String toString() {
        return getSource().toString() + " --> " + getDestination().toString();
    }
}
