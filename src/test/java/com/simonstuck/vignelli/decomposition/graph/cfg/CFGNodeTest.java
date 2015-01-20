package com.simonstuck.vignelli.decomposition.graph.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CFGNodeTest {

    public static final int TEST_ID = 1;
    private PsiStatement statement;
    private CFGNode node;

    @Before
    public void setUp() throws Exception {
        statement = mock(PsiStatement.class);
        node = new CFGNode(TEST_ID, statement);
    }

    @Test
    public void newNodeShouldNotContainAnyIncomingOrOutGoingEdges() throws Exception {
        assertTrue(node.getIncomingEdges().size() == 0);
        assertTrue(node.getOutgoingEdges().size() == 0);
    }

    @Test
    public void nodeWithNoIncomingEdgesShouldBeALeader() throws Exception {
        assertTrue(node.isLeader());
    }

    @Test
    public void pathJoiningNodeShouldBeALeader() throws Exception {
        node.addIncomingEdge((GraphEdge<CFGNode>) mock(GraphEdge.class));
        node.addIncomingEdge((GraphEdge<CFGNode>) mock(GraphEdge.class));
        assertTrue(node.isLeader());
    }

    @Test
    public void nodeWithLessThanTwoOutgoingEdgesIsNotABranch() throws Exception {
        assertFalse(node.isBranch());
    }

    @Test
    public void nodeWithAtLeastTwoOutgoingEdgesIsBranchNode() throws Exception {
        node.addOutgoingEdge((GraphEdge<CFGNode>) mock(GraphEdge.class));
        node.addOutgoingEdge((GraphEdge<CFGNode>) mock(GraphEdge.class));
        assertTrue(node.isBranch());

        // Try with more than two
        node.addOutgoingEdge((GraphEdge<CFGNode>) mock(GraphEdge.class));
        assertTrue(node.isBranch());
    }

    @Test
    public void nodeThatImmediatelyFollowsABranchNodeShouldBeALeader() throws Exception {
        CFGNode branchNode = mock(CFGNode.class);
        when(branchNode.isBranch()).thenReturn(true);
        GraphEdge<CFGNode> incomingBranchEdge = (GraphEdge<CFGNode>) mock(GraphEdge.class);
        when(incomingBranchEdge.getSource()).thenReturn(branchNode);
        node.addIncomingEdge(incomingBranchEdge);

        assertTrue(node.isLeader());
    }

    @Test
    public void nodeWithOneIncomingEdgeFromNonLeaderShouldNotBeALeader() throws Exception {
        CFGNode branchNode = mock(CFGNode.class);
        when(branchNode.isBranch()).thenReturn(false);
        GraphEdge<CFGNode> incomingBranchEdge = (GraphEdge<CFGNode>) mock(GraphEdge.class);
        when(incomingBranchEdge.getSource()).thenReturn(branchNode);
        node.addIncomingEdge(incomingBranchEdge);
        assertFalse(node.isLeader());
    }

    @Test
    public void nodesWithDifferentIdsHaveDifferentHashCodes() throws Exception {
        CFGNode otherNode = new CFGNode(TEST_ID + 1, statement);
        assertNotEquals(node.hashCode(), otherNode.hashCode());
    }

    @Test
    public void nodeWithHigherIdShouldBeLarger() throws Exception {
        CFGNode otherNode = new CFGNode(TEST_ID - 1, statement);
        assertEquals(node.compareTo(otherNode), 1);
    }

    @Test
    public void nodeWithLowerIdShouldBeSmaller() throws Exception {
        CFGNode otherNode = new CFGNode(TEST_ID + 1, statement);
        assertEquals(node.compareTo(otherNode), -1);

    }

    @Test
    public void nodeWithSameIdShouldBeSame() throws Exception {
        assertEquals(node.compareTo(node), 0);
    }
}