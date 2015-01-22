package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlock;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class PDGSliceUnion {
    private final ProgramDependenceGraph pdg;
    private final Set<PDGNode> nodeCriteria;
    private final Set<PDGNode> sliceNodes;
    private final PDGSlice subgraph;
    private final Set<PsiVariable> passedParameters;
    private final Set<PDGNode> indispensableNodes;
    private final Set<PDGNode> removableNodes;

    public Set<PsiVariable> getPassedParameters() {
        return new TreeSet<PsiVariable>(passedParameters);
    }

    public Set<PDGNode> getIndispensableNodes() {
        return new TreeSet<PDGNode>(indispensableNodes);
    }

    public Set<PDGNode> getRemovableNodes() {
        return new TreeSet<PDGNode>(removableNodes);
    }

    public PDGSliceUnion(ProgramDependenceGraph pdg, BasicBlock boundaryBlock, Set<PDGNode> nodeCriteria, PsiVariable objectReference) {
        this.pdg = pdg;
        this.nodeCriteria = nodeCriteria;
        this.subgraph = new PDGSlice(pdg, boundaryBlock);

        // Compute slice nodes
        this.sliceNodes = new TreeSet<PDGNode>();
        for (PDGNode nodeCriterion : nodeCriteria) {
            sliceNodes.addAll(subgraph.computeSlice(nodeCriterion));
        }

        Set<PDGNode> remainingNodes = new TreeSet<PDGNode>();
        remainingNodes.add(pdg.getMethodEntryNode());
        for (PDGNode node : pdg.getNodes()) {
            if (!sliceNodes.contains(node)) {
                remainingNodes.add(node);
            }
        }


        this.passedParameters = new LinkedHashSet<PsiVariable>();
        Set<PDGNode> nCD = new LinkedHashSet<PDGNode>();
        Set<PDGNode> nDD = new LinkedHashSet<PDGNode>();

        //TODO: What's going on here?!
        for (GraphEdge<PDGNode> edge : pdg.getEdges()) {
            PDGDependence dependence = (PDGDependence)edge;

            if (dependence.getType() == PDGDependence.PDGDependenceType.DATA) {
                PDGDataDependence dataDep = (PDGDataDependence) dependence;
                if (remainingNodes.contains(dataDep.getSource()) && sliceNodes.contains(dataDep.getDestination())) {
                    passedParameters.add(dataDep.getData());
                }
                if (sliceNodes.contains(dataDep.getSource()) && remainingNodes.contains(dataDep.getDestination()) && !dataDep.getData().equals(objectReference)) {
                    nDD.add(dataDep.getSource());
                }
            } else if (dependence.getType() == PDGDependence.PDGDependenceType.CONTROL) {
                if (sliceNodes.contains(dependence.getSource()) && remainingNodes.contains(dependence.getDestination())) {
                    nCD.add(dependence.getSource());
                }
            }
        }

        System.out.println("nCD = " + nCD);
        System.out.println("nDD = " + nDD);



        Set<PDGNode> controlIndispensableNodes = new LinkedHashSet<PDGNode>();
        for (PDGNode p : nCD) {
            for (PsiVariable usedVariable : p.getUsedVariables()) {
                Set<PDGNode> pSliceNodes = subgraph.computeSlice(p, usedVariable);
                for (PDGNode q : pdg.getNodes()) {
                    if (pSliceNodes.contains(q) || q.equals(p)) {
                        controlIndispensableNodes.add(q);
                    }
                }
            }
            if (p.usedVariables.isEmpty()) {
                Set<PDGNode> pSliceNodes = subgraph.computeSlice(p);
                for (PDGNode q : pdg.getNodes()) {
                    if (pSliceNodes.contains(q) || q.equals(p)) {
                        controlIndispensableNodes.add(q);
                    }
                }
            }
        }
        Set<PDGNode> dataIndispensableNodes = new LinkedHashSet<PDGNode>();
        for (PDGNode p : nDD) {
            for (PsiVariable definedVariable : p.getDefinedVariables()) {
                Set<PDGNode> pSliceNodes = subgraph.computeSlice(p, definedVariable);
                for (PDGNode q : pdg.getNodes()) {
                    if (pSliceNodes.contains(q)) {
                        dataIndispensableNodes.add(q);
                    }
                }
            }
        }
        this.indispensableNodes = new TreeSet<PDGNode>();
        indispensableNodes.addAll(controlIndispensableNodes);
        indispensableNodes.addAll(dataIndispensableNodes);


        this.removableNodes = new LinkedHashSet<PDGNode>();
        for(PDGNode node : pdg.getNodes()) {
            if(!remainingNodes.contains(node) && !indispensableNodes.contains(node)) {
                removableNodes.add(node);
            }
        }
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
                ", passedParameters=" + passedParameters +
                ", indispensableNodes=" + indispensableNodes +
                ", removableNodes=" + removableNodes +
                '}';
    }
}
