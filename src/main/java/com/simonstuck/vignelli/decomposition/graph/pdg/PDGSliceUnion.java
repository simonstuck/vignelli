package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlock;

import java.util.Set;
import java.util.TreeSet;

public class PDGSliceUnion {
    private final ProgramDependenceGraph pdg;
    private final Set<PDGNode> nodeCriteria;
    private final Set<PDGNode> sliceNodes;
    private final PDGSlice subgraph;

    public PDGSliceUnion(ProgramDependenceGraph pdg, BasicBlock boundaryBlock, Set<PDGNode> nodeCriteria, PsiLocalVariable variableCriterion) {
        this.pdg = pdg;
        this.nodeCriteria = nodeCriteria;
        this.subgraph = new PDGSlice(pdg, boundaryBlock);

        // Compute slice nodes
        this.sliceNodes = new TreeSet<PDGNode>();
        for (PDGNode nodeCriterion : nodeCriteria) {
            sliceNodes.addAll(subgraph.computeSlice(nodeCriterion));
        }



//        Set<PDGNode> nCD = new LinkedHashSet<PDGNode>();
//        Set<PDGNode> nDD = new LinkedHashSet<PDGNode>();
//        for(GraphEdge edge : pdg.edges) {
//            PDGDependence dependence = (PDGDependence)edge;
//            PDGNode srcPDGNode = (PDGNode)dependence.src;
//            PDGNode dstPDGNode = (PDGNode)dependence.dst;
//            if(dependence instanceof PDGDataDependence) {
//                PDGDataDependence dataDependence = (PDGDataDependence)dependence;
//                if(remainingNodes.contains(srcPDGNode) && sliceNodes.contains(dstPDGNode))
//                    passedParameters.add(dataDependence.getData());
//                if(sliceNodes.contains(srcPDGNode) && remainingNodes.contains(dstPDGNode) &&
//                        !dataDependence.getData().equals(localVariableCriterion))
//                    nDD.add(srcPDGNode);
//            }
//            else if(dependence instanceof PDGControlDependence) {
//                if(sliceNodes.contains(srcPDGNode) && remainingNodes.contains(dstPDGNode))
//                    nCD.add(srcPDGNode);
//            }
//        }
//        Set<PDGNode> controlIndispensableNodes = new LinkedHashSet<PDGNode>();
//        for(PDGNode p : nCD) {
//            for(AbstractVariable usedVariable : p.usedVariables) {
//                Set<PDGNode> pSliceNodes = subgraph.computeSlice(p, usedVariable);
//                for(GraphNode node : pdg.nodes) {
//                    PDGNode q = (PDGNode)node;
//                    if(pSliceNodes.contains(q) || q.equals(p))
//                        controlIndispensableNodes.add(q);
//                }
//            }
//            if(p.usedVariables.isEmpty()) {
//                Set<PDGNode> pSliceNodes = subgraph.computeSlice(p);
//                for(GraphNode node : pdg.nodes) {
//                    PDGNode q = (PDGNode)node;
//                    if(pSliceNodes.contains(q) || q.equals(p))
//                        controlIndispensableNodes.add(q);
//                }
//            }
//        }
//        Set<PDGNode> dataIndispensableNodes = new LinkedHashSet<PDGNode>();
//        for(PDGNode p : nDD) {
//            for(AbstractVariable definedVariable : p.definedVariables) {
//                Set<PDGNode> pSliceNodes = subgraph.computeSlice(p, definedVariable);
//                for(GraphNode node : pdg.nodes) {
//                    PDGNode q = (PDGNode)node;
//                    if(pSliceNodes.contains(q))
//                        dataIndispensableNodes.add(q);
//                }
//            }
//        }
//        this.indispensableNodes = new TreeSet<PDGNode>();
//        indispensableNodes.addAll(controlIndispensableNodes);
//        indispensableNodes.addAll(dataIndispensableNodes);
//        Set<PDGNode> throwStatementNodesToBeAddedToDuplicatedNodesDueToIndispensableNodes = new TreeSet<PDGNode>();
//        for(PDGNode throwNode : throwStatementNodes) {
//            for(PDGNode indispensableNode : indispensableNodes) {
//                if(isNestedInside(throwNode, indispensableNode)) {
//                    throwStatementNodesToBeAddedToDuplicatedNodesDueToIndispensableNodes.add(throwNode);
//                    break;
//                }
//            }
//        }
//        for(PDGNode throwNode : throwStatementNodesToBeAddedToDuplicatedNodesDueToRemainingNodes) {
//            indispensableNodes.addAll(subgraph.computeSlice(throwNode));
//        }
//        for(PDGNode throwNode : throwStatementNodesToBeAddedToDuplicatedNodesDueToIndispensableNodes) {
//            indispensableNodes.addAll(subgraph.computeSlice(throwNode));
//        }
//        this.removableNodes = new LinkedHashSet<PDGNode>();
//        for(GraphNode node : pdg.nodes) {
//            PDGNode pdgNode = (PDGNode)node;
//            if(!remainingNodes.contains(pdgNode) && !indispensableNodes.contains(pdgNode))
//                removableNodes.add(pdgNode);
//        }
    }

    private boolean isSliceEqualToMethodBody() {
        return false;
    }

    private boolean sliceContainsOnlyOneNodeCriterionAndDeclarationOfVariableCriterion() {
        return false;
    }

    private boolean declarationOfVariableCriterionIsDuplicated() {
        return false;
    }

    private boolean variableCriterionIsReturnedVariableInOriginalMethod() {
        return false;
    }

    private boolean allNodeCriteriaAreDuplicated() {
        return false;
    }

    private boolean returnStatementIsControlDependentOnSliceNode() {
        return false;
    }

    private boolean sliceContainsReturnStatement() {
        return false;
    }

    private boolean containsDuplicateNodeWithStateChangingMethodInvocation() {
        return false;
    }

    private boolean nonDuplicatedSliceNodeAntiDependsOnNonRemovableNode() {
        return false;
    }

    private boolean nonDuplicatedSliceNodeOutputDependsOnNonRemovableNode() {
        return false;
    }

    private boolean duplicatedSliceNodeWithClassInstantiationHasDependenceOnRemovableNode() {
        return false;
    }

    private boolean complyWithUserThresholds() {
        return true;
    }

    private boolean sliceContainsBranchStatementWithoutInnermostLoop() {
        return false;
    }

    /**
     * Checks whether this slice union satisfies the rules to be a valid extraction candidate.
     * @return true iff it is valid
     */
    public boolean isValid() {
        return     !isSliceEqualToMethodBody()
                && !sliceContainsOnlyOneNodeCriterionAndDeclarationOfVariableCriterion()
                && !declarationOfVariableCriterionIsDuplicated()
                && !variableCriterionIsReturnedVariableInOriginalMethod()
                && sliceNodes.size() >= nodeCriteria.size()
                && !allNodeCriteriaAreDuplicated()
                && !returnStatementIsControlDependentOnSliceNode()
                && !sliceContainsReturnStatement()
                && !containsDuplicateNodeWithStateChangingMethodInvocation()
                && !nonDuplicatedSliceNodeAntiDependsOnNonRemovableNode()
                && !nonDuplicatedSliceNodeOutputDependsOnNonRemovableNode()
                && !duplicatedSliceNodeWithClassInstantiationHasDependenceOnRemovableNode()
                && !sliceContainsBranchStatementWithoutInnermostLoop()
                && complyWithUserThresholds();
    }

    @Override
    public String toString() {
        return "PDGSliceUnion{" +
                "pdg=" + pdg +
                ", nodeCriteria=" + nodeCriteria +
                ", sliceNodes=" + sliceNodes +
                ", subgraph=" + subgraph +
                '}';
    }
}
