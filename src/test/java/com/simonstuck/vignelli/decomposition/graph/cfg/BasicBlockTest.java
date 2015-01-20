package com.simonstuck.vignelli.decomposition.graph.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class BasicBlockTest {

    public static final int TEST_ID = 1;
    private BasicBlock block;
    private CFGNode leader;
    private List<CFGNode> otherNodes;

    @Before
    public void setUp() throws Exception {
        leader = mock(CFGNode.class);
        otherNodes = new LinkedList<CFGNode>();

        block = new BasicBlock(TEST_ID, leader);

        for (int i = 0; i < 5; i++) {
            CFGNode node = mock(CFGNode.class);
            otherNodes.add(node);
            block.add(node);
        }

    }

    @Test
    public void getsIdItWasInitialisedWith() throws Exception {
        assertEquals(block.getId(), TEST_ID);
    }

    @Test
    public void newBasicBlockShouldNotContainAnyOtherNodes() throws Exception {
        block = new BasicBlock(TEST_ID, leader);
        assertTrue(block.getNodes().isEmpty());
    }

    @Test
    public void returnsTheLeaderItWasInitialisedWith() throws Exception {
        assertEquals(block.getLeader(), leader);
    }

    @Test
    public void shouldReturnAllNodesExcludingLeader() throws Exception {
        assertTrue(block.getNodes().containsAll(otherNodes));
        assertFalse(block.getNodes().contains(leader));
    }

    @Test
    public void getLastNodeShouldReturnLastNode() throws Exception {
        assertEquals(block.getLastNode(), otherNodes.get(otherNodes.size() - 1));
    }

    @Test
    public void getLastNodeShouldReturnLeaderIfNoOtherNodesArePresentInBlock() throws Exception {
        block = new BasicBlock(TEST_ID, leader);
        assertEquals(block.getLastNode(), leader);
    }

    @Test
    public void getAllNodesShouldReturnAllNodesIncludingTheLeader() throws Exception {
        assertTrue(block.getAllNodes().containsAll(otherNodes));
        assertTrue(block.getAllNodes().contains(leader));
    }

    @Test
    public void representationShouldContainTheLeader() throws Exception {
        assertTrue(block.toString().contains(leader.toString()));
    }
}