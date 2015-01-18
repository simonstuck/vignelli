package com.simonstuck.vignelli.decomposition.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GraphEdgeTest {
    private GraphNode src;
    private GraphNode dest;

    private GraphEdge edge;

    @Before
    public void setUp() {
        src = mock(GraphNode.class);
        dest = mock(GraphNode.class);
        edge = new GraphEdge(src, dest);
    }

    @Test
    public void shouldReturnCorrectDestination() throws Exception {
        assertEquals(edge.getDestination(), dest);
    }

    @Test
    public void shouldReturnCorrectSource() throws Exception {
        assertEquals(edge.getSource(), src);
    }
}