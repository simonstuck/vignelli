package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.decomposition.graph.Graph;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.GraphNodeIdGenerator;
import com.simonstuck.vignelli.decomposition.graph.cfg.AugmentedControlFlowGraph;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlock;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlockCollection;
import com.simonstuck.vignelli.decomposition.graph.cfg.CFGNode;
import com.simonstuck.vignelli.utils.IdGenerator;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ProgramDependenceGraph extends Graph<PDGNode> {

    private final Set<PsiLocalVariable> localVariables;
    private final IdGenerator<Integer> idGenerator;
    private final PDGMethodEntryNode methodEntryNode;

    private final BiMap<CFGNode, PDGNode> controlFlowDependenceNodeMapping;
    // Cache dominator computation as all nodes that are part of the same block have the
    // same dominator
    private final Map<PDGNode, Set<BasicBlock>> dominationMapping;

    private final BasicBlockCollection bbc;

    /**
     * Creates a new program dependence graph using the supplied control dependence graph.
     * @param cfg The augmented control dependence graph to use
     */
    public ProgramDependenceGraph(AugmentedControlFlowGraph cfg, BasicBlockCollection bbc) {
        this.bbc = bbc;
        controlFlowDependenceNodeMapping = HashBiMap.create(cfg.getSize());
        dominationMapping = new HashMap<PDGNode, Set<BasicBlock>>(cfg.getSize());

        idGenerator = new GraphNodeIdGenerator();
        localVariables = getLocalVariables(cfg);
        methodEntryNode = new PDGMethodEntryNode(idGenerator.generateId(), cfg.getMethod());
        addNode(methodEntryNode);

        createControlDependenciesFromEntryNode(cfg);
    }

    private Set<PsiLocalVariable> getLocalVariables(AugmentedControlFlowGraph cfg) {
        LocalVariableDeclarationCollectorVisitor variableCollector = new LocalVariableDeclarationCollectorVisitor();
        variableCollector.visitMethod(cfg.getMethod());
        return variableCollector.getLocalVariables();
    }

    private void createControlDependenciesFromEntryNode(AugmentedControlFlowGraph cfg) {
        for (CFGNode node : cfg.getNodes()) {
            processCFGNode(methodEntryNode, node);
        }
    }

    private void processCFGNode(PDGNode previousNode, CFGNode cfgNode) {
        PDGNode pdgNode = new PDGStatementNode(idGenerator.generateId(), cfgNode, localVariables);
        addNode(pdgNode);
        // Add mapping from old cfg node to new pdg node so that we do not have to traverse the
        // graph every time we want to find a
        controlFlowDependenceNodeMapping.put(cfgNode, pdgNode);

        PDGControlDependence controlDependence = new PDGControlDependence(previousNode, pdgNode);
        addEdge(controlDependence);
    }

    private PDGNode getPDGNode(CFGNode cfgNode) {
        return controlFlowDependenceNodeMapping.get(cfgNode);
    }

    /**
     * Gets the node that directly dominates the leader of the given block.
     * @param block The block to consider
     * @return The direct dominator (either branch or method entry)
     */
    private PDGNode getDirectDominator(BasicBlock block) {
        // TODO: Add branch logic here
        PDGNode leader = getPDGNode(block.getLeader());
        for (GraphEdge<PDGNode> edge : leader.getIncomingEdges()) {
            PDGDependence dependence = (PDGDependence) edge;
            if (dependence.getType() == PDGDependence.PDGDependenceType.CONTROL) {
                return dependence.getSource();
            }
        }
        assert false; // We should never reach this point here

        return null;
    }

    private Set<BasicBlock> getDominatedBlocks(BasicBlock block) {
        PDGNode dominator = getDirectDominator(block);

        // Cache results
        Set<BasicBlock> dominatedBlocks = dominationMapping.get(dominator);
        if (dominatedBlocks == null) {
            dominatedBlocks = getDominatedBlocks(dominator);
            dominationMapping.put(dominator, dominatedBlocks);
        }
        return dominatedBlocks;
    }


    private Set<BasicBlock> getDominatedBlocks(PDGNode leader) {
        Set<BasicBlock> dominatedBlocks = new HashSet<BasicBlock>();

        Deque<PDGNode> nodesToConsider = new LinkedList<PDGNode>();
        nodesToConsider.push(leader);

        while (!nodesToConsider.isEmpty()) {
            PDGNode node = nodesToConsider.pop();

            for (GraphEdge<PDGNode> edge : node.getOutgoingEdges()) {
                PDGDependence dependence = (PDGDependence)edge;
                if (dependence.getType() == PDGDependence.PDGDependenceType.CONTROL) {
                    // Find the basic block associated with the destination of the dependence
                    PDGNode destination = dependence.getDestination();
                    CFGNode cfgDest = controlFlowDependenceNodeMapping.inverse().get(destination);
                    BasicBlock dstBlock = bbc.getBasicBlock(cfgDest);
                    dominatedBlocks.add(dstBlock);

                    // Now add the last node to the nodes to consider
                    PDGNode lastNode = controlFlowDependenceNodeMapping.get(dstBlock.getLastNode());
                    nodesToConsider.add(lastNode);
                }
            }
        }
        return dominatedBlocks;
    }

    /**
     * Gets the boundary blocks for the given node.
     * @param node The node for which to find the boundary blocks
     * @return A new set with all boundary blocks
     */
    public Set<BasicBlock> getBoundaryBlocks(PDGNode node) {
        CFGNode cfgNode = controlFlowDependenceNodeMapping.inverse().get(node);
        Set<BasicBlock> boundaryBlocks = new LinkedHashSet<BasicBlock>();
        BasicBlock srcBlock = bbc.getBasicBlock(cfgNode);

        for (BasicBlock block : bbc.getBasicBlocks()) {
            Set<BasicBlock> forwardReachableBlocks = bbc.forwardReachableBlocks(block);
            Set<BasicBlock> dominatedBlocks = getDominatedBlocks(block);

            // A boundary block A for block B is a block whose forward-reachable and dominated blocks'
            // intersection contain block B.
            Set<BasicBlock> intersection = new LinkedHashSet<BasicBlock>(forwardReachableBlocks);
            intersection.retainAll(dominatedBlocks);
            if (intersection.contains(srcBlock)) {
                boundaryBlocks.add(block);
            }
        }
        return boundaryBlocks;
    }

    /**
     * Gets the PDG nodes that are part of the reachable region starting from the given block.
     * @param block The starting block from which to find all reachable nodes
     * @return A new set with all reachable nodes
     */
    public Set<PDGNode> getBlockBasedRegion(BasicBlock block) {
        Set<PDGNode> regionNodes = new LinkedHashSet<PDGNode>();
        Set<BasicBlock> reachableBlocks = bbc.forwardReachableBlocks(block);
        for (BasicBlock reachableBlock : reachableBlocks) {
            for (CFGNode cfgNode : reachableBlock.getAllNodes()) {
                regionNodes.add(controlFlowDependenceNodeMapping.get(cfgNode));
            }
        }
        return regionNodes;
    }

    private class LocalVariableDeclarationCollectorVisitor extends JavaRecursiveElementVisitor {

        private final Set<PsiLocalVariable> localVariables = new HashSet<PsiLocalVariable>();

        /**
         * Gets the local variables that were collected.
         * @return A new set with all the local variables.
         */
        public Set<PsiLocalVariable> getLocalVariables() {
            return new HashSet<PsiLocalVariable>(localVariables);
        }

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            localVariables.add(variable);
            super.visitLocalVariable(variable);
        }
    }
}
