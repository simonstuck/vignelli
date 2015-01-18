package com.simonstuck.vignelli.decomposition.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GraphNodeTest {

    public static final int TEST_ID  = 42;
    private GraphNode node;

    @Before
    public void setUp() {
        node = new GraphNode(TEST_ID);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(node.getId(), TEST_ID);
    }

    @Test
    public void addingOneIncomingEdgeShouldResultInOneEdgeBeingSaved() throws Exception {
        GraphNode otherNode = new GraphNode(TEST_ID + 1);
        GraphEdge edge = new GraphEdge(otherNode, node);
        node.addIncomingEdge(edge);
        assertEquals(node.getIncomingEdges().size(), 1);
        assertEquals(node.getIncomingEdges().iterator().next(), edge);
    }

    @Test
    public void addingOneOutgoingEdgeShouldResultInOneOutgoingEdgeBeingSaved() throws Exception {
        GraphNode otherNode = new GraphNode(TEST_ID + 1);
        GraphEdge edge = new GraphEdge(node, otherNode);
        node.addOutgoingEdge(edge);
        assertEquals(node.getOutgoingEdges().size(), 1);
        assertEquals(node.getOutgoingEdges().iterator().next(), edge);
    }
}