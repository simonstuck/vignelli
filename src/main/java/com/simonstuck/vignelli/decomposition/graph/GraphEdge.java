package com.simonstuck.vignelli.decomposition.graph;

public class GraphEdge<T extends GraphNode> {
    private final T src;
    private final T dst;

    public GraphEdge(T src, T dst) {
        this.src = src;
        this.dst = dst;
    }

    public T getSource() {
        return src;
    }

    public T getDestination() {
        return dst;
    }

    @Override
    public String toString() {
        return getSource().toString() + " --> " + getDestination().toString();
    }
}
