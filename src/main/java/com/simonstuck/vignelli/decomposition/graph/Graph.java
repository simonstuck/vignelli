package com.simonstuck.vignelli.decomposition.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class Graph {
    private final Set<GraphNode> nodes;
    private final Set<GraphEdge> edges;

    public Graph() {
        this.nodes = new LinkedHashSet<GraphNode>();
        this.edges = new LinkedHashSet<GraphEdge>();
    }

    public Set<GraphNode> getNodes() {
        return nodes;
    }

    public Set<GraphEdge> getEdges() {
        return edges;
    }

    public void addNode(GraphNode node) {
        nodes.add(node);
    }

    public void addEdge(GraphEdge edge) {
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
