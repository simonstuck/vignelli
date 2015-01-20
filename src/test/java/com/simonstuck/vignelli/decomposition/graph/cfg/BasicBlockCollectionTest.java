package com.simonstuck.vignelli.decomposition.graph.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class BasicBlockCollectionTest {

    private AugmentedControlFlowGraph graph;
    private LinkedList<CFGNode> graphNodes;
    private BasicBlockCollection collection;

    @Before
    public void setUp() throws Exception {
        CFGNode leader = mock(CFGNode.class);
        when(leader.isLeader()).thenReturn(true);
        when(leader.getId()).thenReturn(0);
        when(leader.compareTo(any(CFGNode.class))).thenCallRealMethod();


        graphNodes = new LinkedList<CFGNode>();
        graphNodes.add(leader);
        for (int i = 1; i < 5; i++) {
            CFGNode node = mock(CFGNode.class);
            when(node.getId()).thenReturn(i);
            when(node.compareTo(any(CFGNode.class))).thenCallRealMethod();
            graphNodes.add(node);
        }

        graph = mock(AugmentedControlFlowGraph.class);
        when(graph.getNodes()).thenReturn(new HashSet<CFGNode>(graphNodes));

        collection = new BasicBlockCollection(graph);
    }

    @Test
    public void returnsOneBasicBlockIfOneLeaderIsGiven() throws Exception {
        assertEquals(collection.getBasicBlocks().size(), 1);
    }

    @Test
    public void forwardReachableBlocksIncludesGivenBlock() throws Exception {
        BasicBlock startingBlock = collection.getBasicBlocks().get(0);
        Set<BasicBlock> blocks = collection.forwardReachableBlocks(startingBlock);
        assertTrue(blocks.contains(startingBlock));
    }

    @Test
    public void stringRepresentationMustIncludeAllBasicBlocks() throws Exception {
        for (BasicBlock block : collection.getBasicBlocks()) {
            assertTrue(collection.toString().contains(block.toString()));
        }
    }
}