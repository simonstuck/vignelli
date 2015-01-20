package com.simonstuck.vignelli.decomposition.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class Graph<T extends GraphNode> {
    private final Set<T> nodes;
    private final Set<GraphEdge<T>> edges;

    public Graph() {
        this.nodes = new LinkedHashSet<T>();
        this.edges = new LinkedHashSet<GraphEdge<T>>();
    }

    public Set<T> getNodes() {
        return nodes;
    }

    public Set<GraphEdge<T>> getEdges() {
        return edges;
    }

    public void addNode(T node) {
        nodes.add(node);
    }

    public void addEdge(GraphEdge<T> edge) {
        edges.add(edge);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes: ");
        for (GraphNode node : getNodes()) {
            sb.append(node);
            sb.append(", ");
        }

        sb.append("\n");
        sb.append("Edges: ");
        for (GraphEdge edge : getEdges()) {
            sb.append(edge);
            sb.append(", ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
