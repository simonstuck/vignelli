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
    public int hashCode() {
        return getSource().hashCode() & getDestination().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!this.getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        GraphEdge<T> other = (GraphEdge<T>) obj;
        return getSource().equals(other.getSource()) && getDestination().equals(other.getDestination());
    }

    @Override
    public String toString() {
        return getSource().toString() + " --> " + getDestination().toString();
    }
}
