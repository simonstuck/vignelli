package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.decomposition.graph.Graph;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlock;

import javax.xml.crypto.Data;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class PDGSlice extends Graph<PDGNode> {
    private final ProgramDependenceGraph pdg;
    private final BasicBlock boundaryBlock;

    public PDGSlice(ProgramDependenceGraph pdg, BasicBlock boundaryBlock) {
        this.pdg = pdg;
        this.boundaryBlock = boundaryBlock;

        for (PDGNode node : pdg.getBlockBasedRegion(boundaryBlock)) {
            addNode(node);
        }

        for (GraphEdge<PDGNode> edge : pdg.getEdges()) {
            // TODO: Deal with other dependencies here
            if (getNodes().contains(edge.getSource()) && getNodes().contains(edge.getDestination())) {
                addEdge(edge);
            }
        }
    }

    public Set<PDGNode> computeSlice(PDGNode nodeCriterion) {
        Set<PDGNode> sliceNodes = new LinkedHashSet<PDGNode>();

        Deque<PDGNode> nodesToConsider = new LinkedList<PDGNode>();
        nodesToConsider.push(nodeCriterion);

        while (!nodesToConsider.isEmpty()) {
            PDGNode currentNode = nodesToConsider.pop();
            sliceNodes.add(currentNode);

            for (GraphEdge<PDGNode> edge : currentNode.getIncomingEdges()) {
                PDGDependence dependence = (PDGDependence) edge;
                //TODO: Fix this for different kinds of dependence
                if (getEdges().contains(dependence)) {
                    PDGNode src = dependence.getSource();
                    if (!sliceNodes.contains(src)) {
                        nodesToConsider.push(src);
                    }
                }
            }
        }
        return sliceNodes;
    }

    public Set<PDGNode> computeSlice(PDGNode nodeCriterion, PsiVariable localVariableCriterion) {
        Set<PDGNode> sliceNodes = new LinkedHashSet<PDGNode>();

        if (nodeCriterion.definesLocalVariable(localVariableCriterion)) {
            sliceNodes.addAll(computeSlice(nodeCriterion));
        } else if (nodeCriterion.usesLocalVariable(localVariableCriterion)) {
            Set<PDGNode> defNodes = getDefNodes(nodeCriterion, localVariableCriterion);
            for (PDGNode defNode : defNodes) {
                sliceNodes.addAll(computeSlice(defNode));
            }
            if (defNodes.isEmpty()) {
                sliceNodes.addAll(computeSlice(nodeCriterion));
            }
        }
        return sliceNodes;
    }

    private Set<PDGNode> getDefNodes(PDGNode node, PsiVariable localVariable) {
        Set<PDGNode> defNodes = new LinkedHashSet<PDGNode>();
        for(GraphEdge<PDGNode> edge : node.getIncomingEdges()) {
            PDGDependence dependence = (PDGDependence)edge;
            if(getEdges().contains(dependence) && dependence.getType() == PDGDependence.PDGDependenceType.DATA) {
                PDGDataDependence dataDependence = (PDGDataDependence)dependence;
                if(dataDependence.getData().equals(localVariable)) {
                    defNodes.add(dependence.getSource());
                }
            }
        }
        return defNodes;
    }

    @Override
    public String toString() {
        return "PDGSlice{pdg=" + pdg + ", boundaryBlock=" + boundaryBlock + '}';
    }
}

