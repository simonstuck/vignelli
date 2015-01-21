package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.decomposition.ast.util.PsiMethodUtil;
import com.simonstuck.vignelli.decomposition.graph.Graph;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.GraphNode;
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
        localVariables = PsiMethodUtil.getLocalVariables(cfg.getMethod());
        methodEntryNode = new PDGMethodEntryNode(idGenerator.generateId(), cfg.getMethod());

        createControlDependenciesFromEntryNode(cfg);
        createDataDependencies();
    }

    private void createDataDependencies() {
        PDGNode firstPDGNode = getNodes().iterator().next();

        for (PsiVariable variable : methodEntryNode.getDefinedVariables()) {
            if (firstPDGNode.usesLocalVariable(variable)) {
                PDGDataDependence dataDependence = new PDGDataDependence(methodEntryNode, firstPDGNode, variable);
                addEdge(dataDependence);
            }

            if (!firstPDGNode.definesLocalVariable(variable)) {
                dataDependenceSearch(methodEntryNode, variable, firstPDGNode, new LinkedHashSet<PDGNode>());
            } else if (methodEntryNode.declaresLocalVariable(variable)) {
                // create def-order data dependence edge
                PDGDataDependence dataDependence = new PDGDataDependence(methodEntryNode, firstPDGNode, variable);
                addEdge(dataDependence);
            }
        }

        for (PDGNode node : getNodes()) {
            for (PsiVariable variable : node.getDefinedVariables()) {
                dataDependenceSearch(node, variable, firstPDGNode, new LinkedHashSet<PDGNode>());
                outputDependenceSearch(node, variable, firstPDGNode, new LinkedHashSet<PDGNode>());
            }
            for (PsiVariable variable : node.getUsedVariables()) {
                antiDependenceSearch(node, variable, firstPDGNode, new LinkedHashSet<PDGNode>());
            }
        }
    }

    // FIXME: Refactor this for all three methods
    private void dataDependenceSearch(PDGNode initialNode, PsiVariable variableInstruction, PDGNode currentNode, Set<PDGNode> visitedNodes) {
        if (visitedNodes.contains(currentNode)) {
            return;
        }

        System.out.println("data dependence search");
        visitedNodes.add(currentNode);
        CFGNode currentCFGNode = controlFlowDependenceNodeMapping.inverse().get(currentNode);
        System.out.println("Current CFG node: " + currentCFGNode + " -- " + currentCFGNode.getStatement());
        System.out.println("outgoing: " + currentCFGNode.getOutgoingEdges());
        System.out.println("");

        for (GraphEdge<CFGNode> edge : currentCFGNode.getOutgoingEdges()) {
            System.out.println("Checking out edge: " + edge);
            //TODO: Fix this for loopback
            PDGNode dstPDGNode = controlFlowDependenceNodeMapping.get(edge.getDestination());
            if (dstPDGNode.usesLocalVariable(variableInstruction)) {
                System.out.println("we're using itL " + variableInstruction);
                PDGDataDependence dataDependence = new PDGDataDependence(initialNode, dstPDGNode, variableInstruction);
                addEdge(dataDependence);
            }
            if (!dstPDGNode.definesLocalVariable(variableInstruction)) {
                dataDependenceSearch(initialNode, variableInstruction, dstPDGNode, visitedNodes);
            } else if (initialNode.declaresLocalVariable(variableInstruction) && !initialNode.equals(dstPDGNode)) {
                // create def-order data dependence
                PDGDataDependence dataDependence = new PDGDataDependence(initialNode, dstPDGNode, variableInstruction);
                addEdge(dataDependence);
            }
        }
    }

    private void antiDependenceSearch(PDGNode initialNode, PsiVariable variableInstruction, PDGNode currentNode, Set<PDGNode> visitedNodes) {
        if (visitedNodes.contains(currentNode)) {
            return;
        }

        visitedNodes.add(currentNode);
        CFGNode currentCFGNode = controlFlowDependenceNodeMapping.inverse().get(currentNode);

        for (GraphEdge<CFGNode> edge : currentCFGNode.getOutgoingEdges()) {
            //TODO: Fix this for loopback
            PDGNode dstPDGNode = controlFlowDependenceNodeMapping.get(edge.getDestination());
            if(dstPDGNode.definesLocalVariable(variableInstruction)) {
                PDGAntiDependence antiDependence = new PDGAntiDependence(initialNode, dstPDGNode, variableInstruction);
                addEdge(antiDependence);
            } else {
                antiDependenceSearch(initialNode, variableInstruction, dstPDGNode, visitedNodes);
            }
        }
    }

    private void outputDependenceSearch(PDGNode initialNode, PsiVariable variableInstruction, PDGNode currentNode, Set<PDGNode> visitedNodes) {
        if (visitedNodes.contains(currentNode)) {
            return;
        }

        visitedNodes.add(currentNode);
        CFGNode currentCFGNode = controlFlowDependenceNodeMapping.inverse().get(currentNode);

        for (GraphEdge<CFGNode> edge : currentCFGNode.getOutgoingEdges()) {
            //TODO: Fix this for loopback
            PDGNode dstPDGNode = controlFlowDependenceNodeMapping.get(edge.getDestination());
            if(dstPDGNode.definesLocalVariable(variableInstruction)) {
                PDGOutputDependence outputDependence = new PDGOutputDependence(initialNode, dstPDGNode, variableInstruction);
                addEdge(outputDependence);
            } else {
                outputDependenceSearch(initialNode, variableInstruction, dstPDGNode, visitedNodes);
            }
        }
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
        // Important: add edges to the nodes, too!
        previousNode.addOutgoingEdge(controlDependence);
        pdgNode.addIncomingEdge(controlDependence);
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
        System.out.println("Leader of block: " + leader);
        for (GraphEdge<PDGNode> edge : leader.getIncomingEdges()) {
            PDGDependence dependence = (PDGDependence) edge;
            if (dependence.getType() == PDGDependence.PDGDependenceType.CONTROL) {
                return dependence.getSource();
            }
        }
//        assert false; // We should never reach this point here

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

    public Set<PDGNode> getAssignmentNodesForVariable(PsiLocalVariable variable) {
        Set<PDGNode> nodeCriteria = new LinkedHashSet<PDGNode>();

        for (PDGNode node : getNodes()) {
            if (node.definesLocalVariable(variable) && !node.declaresLocalVariable(variable)) {
                nodeCriteria.add(node);
            }
        }
        return nodeCriteria;
    }

//    public Set<PDGNode> getAssignmentNodesOfVariableCriterion(AbstractVariable localVariableCriterion) {
//        Set<PDGNode> nodeCriteria = new LinkedHashSet<PDGNode>();
//        for(GraphNode node : nodes) {
//            PDGNode pdgNode = (PDGNode)node;
//            if(pdgNode.definesLocalVariable(localVariableCriterion) &&
//                    !pdgNode.declaresLocalVariable(localVariableCriterion))
//                nodeCriteria.add(pdgNode);
//        }
//        return nodeCriteria;
//    }

}
