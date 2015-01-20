package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.GraphNode;
import org.jetbrains.annotations.NotNull;

public class CFGNode extends GraphNode<CFGNode> implements Comparable<CFGNode> {

    protected final PsiStatement statement;

    public CFGNode(int id, @NotNull PsiStatement statement) {
        super(id);
        this.statement = statement;
    }

    public boolean isLeader() {
        return isFirst() || isJoin() || immediatelyFollowsBranchNode();

    }

    private boolean immediatelyFollowsBranchNode() {
        for (GraphEdge<CFGNode> edge : getIncomingEdges()) {
            CFGNode srcNode = edge.getSource();
            if (srcNode.isBranch()) {
                return true;
            }
        }
        return false;
    }

    public PsiStatement getStatement() {
        return statement;
    }

    private boolean isFirst() {
        return getIncomingEdges().size() == 0;
    }

    public boolean isBranch() {
        return getOutgoingEdges().size() > 1;
    }

    private boolean isJoin() {
        return getIncomingEdges().size() > 1;
    }


    @Override
    public int hashCode() {
        //TODO: This is not too amazing
        return 37 * 17 + this.getId();
    }

    @Override
    public int compareTo(@NotNull CFGNode node) {
        if (getId() > node.getId()) {
            return 1;
        } else if (getId() < node.getId()) {
            return -1;
        } else {
            return 0;
        }
    }
}
