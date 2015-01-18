package com.simonstuck.vignelli.decomposition.graph;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;


public class GraphTest {

    private Graph graph;
    private final Set<GraphNode> nodes = new HashSet<GraphNode>();
    private final Set<GraphEdge> edges = new HashSet<GraphEdge>();

    @Before
    public void setUp() {
        graph = new Graph();
        GraphNode node1 = new GraphNode(1);
        GraphNode node2 = new GraphNode(2);
        GraphNode node3 = new GraphNode(3);
        GraphNode node4 = new GraphNode(4);

        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);

        GraphEdge edge1 = new GraphEdge(node1, node3);
        GraphEdge edge2 = new GraphEdge(node1, node2);
        GraphEdge edge3 = new GraphEdge(node2, node3);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);


        for (GraphNode node : nodes) {
            graph.addNode(node);
        }
        for (GraphEdge edge : edges) {
            graph.addEdge(edge);
        }
    }

    @Test
    public void allNodesThatWereAddedAreAlsoReturned() throws Exception {
        assertTrue(graph.getNodes().containsAll(nodes));
    }

    @Test
    public void noNodesThatWereNotAddedAreReturned() throws Exception {
        Set<GraphNode> returnedNodes = graph.getNodes();
        returnedNodes.removeAll(nodes);
        assertTrue(returnedNodes.isEmpty());
    }

    @Test
    public void allEdgesThatWereAddedAreAlsoReturned() throws Exception {
        assertTrue(graph.getEdges().containsAll(edges));
    }

    @Test
    public void noEdgesThatWereNotAddedAreReturned() throws Exception {
        Set<GraphEdge> returnedEdges = graph.getEdges();
        returnedEdges.removeAll(edges);
        assertTrue(returnedEdges.isEmpty());
    }

    @Test
    public void stringRepresentationContainsAllNodesAndEdges() throws Exception {
        for (GraphNode node : graph.getNodes()) {
            assertTrue(graph.toString().contains(node.toString()));
        }

        for (GraphEdge edge : graph.getEdges()) {
            assertTrue(graph.toString().contains(edge.toString()));
        }
    }
}