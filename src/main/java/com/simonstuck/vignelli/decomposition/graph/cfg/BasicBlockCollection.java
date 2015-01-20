package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.GraphNodeIdGenerator;
import com.simonstuck.vignelli.utils.IdGenerator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BasicBlockCollection {

    private final List<BasicBlock> basicBlocks;
    private final Map<CFGNode, BasicBlock> nodeBlocks;

    /**
     * Creates a new BasicBlockCollection from the given control flow graph.
     * @param cfg The control flow graph to build the basic blocks from
     */
    public BasicBlockCollection(AugmentedControlFlowGraph cfg) {
        this.basicBlocks = new ArrayList<BasicBlock>();
        this.nodeBlocks = new HashMap<CFGNode, BasicBlock>();

        IdGenerator<Integer> idGenerator = new GraphNodeIdGenerator();

        TreeSet<CFGNode> allNodes = new TreeSet<CFGNode>(cfg.getNodes());

        for (CFGNode cfgNode : allNodes) {
            if (cfgNode.isLeader()) {
                BasicBlock basicBlock = new BasicBlock(idGenerator.generateId(), cfgNode);
                basicBlocks.add(basicBlock);
                nodeBlocks.put(cfgNode, basicBlock);
            } else {
                // Otherwise we are looking at a normal node inside the last block
                getLastBasicBlock().add(cfgNode);
                nodeBlocks.put(cfgNode,getLastBasicBlock());
            }
        }
    }

    public List<BasicBlock> getBasicBlocks() {
        return new ArrayList<BasicBlock>(basicBlocks);
    }

    /**
     * Returns all blocks that are reachable from the given block without loopback.
     * @param basicBlock The basic block to start from
     * @return A set of all blocks that are forward-reachable
     */
    public Set<BasicBlock> forwardReachableBlocks(BasicBlock basicBlock) {
        Set<BasicBlock> reachableBlocks = new LinkedHashSet<BasicBlock>();
        Deque<BasicBlock> blocksToConsider = new ArrayDeque<BasicBlock>();
        blocksToConsider.add(basicBlock);

        while (!blocksToConsider.isEmpty()) {
            BasicBlock block = blocksToConsider.pop();
            CFGNode lastNode = block.getLastNode();
            for (GraphEdge<CFGNode> edge : lastNode.getOutgoingEdges()) {
                BasicBlock destBlock = nodeBlocks.get(edge.getDestination());
                // TODO: Check for loopback edge here!
                blocksToConsider.add(destBlock);
            }
            reachableBlocks.add(block);
        }
        return reachableBlocks;
    }

    private BasicBlock getLastBasicBlock() {
        return basicBlocks.get(basicBlocks.size() - 1);
    }

    @Override
    public String toString() {
        return getBasicBlocks().toString();
    }
}