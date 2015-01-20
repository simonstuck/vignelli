package com.simonstuck.vignelli.decomposition.graph.cfg;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private final int id;
    private final CFGNode leader;
    private List<CFGNode> nodes;

    /**
     * Creates a new basic block with the given id and the given leader.
     * @param id unique identifier for this block
     * @param leader The leader for this new block
     */
    public BasicBlock(int id, CFGNode leader) {
        this.id = id;
        this.leader = leader;
        this.nodes = new ArrayList<CFGNode>();
    }

    public int getId() {
        return id;
    }

    public CFGNode getLeader() {
        return leader;
    }

    public List<CFGNode> getNodes() {
        return new ArrayList<CFGNode>(nodes);
    }

    /**
     * Gets all nodes that belong to this basic block.
     * @return A new list of all nodes belonging to this block.
     */
    public List<CFGNode> getAllNodes() {
        List<CFGNode> allNodes = new ArrayList<CFGNode>(nodes.size() + 1);
        allNodes.addAll(nodes);
        allNodes.add(leader);
        return allNodes;
    }

    /**
     * Gets the last node. Returns the leader if it is the only node in this block.
     * @return The last node
     */
    public CFGNode getLastNode() {
        if (!nodes.isEmpty()) {
            return nodes.get(nodes.size() - 1);
        } else {
            return leader;
        }
    }

    public void add(CFGNode node) {
        nodes.add(node);
    }

    @Override
    public String toString() {
        return leader.toString() + nodes.toString();
    }
}