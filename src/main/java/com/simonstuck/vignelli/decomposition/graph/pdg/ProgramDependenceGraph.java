package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.decomposition.graph.Graph;
import com.simonstuck.vignelli.decomposition.graph.GraphNodeIdGenerator;
import com.simonstuck.vignelli.decomposition.graph.cfg.AugmentedControlFlowGraph;
import com.simonstuck.vignelli.decomposition.graph.cfg.CFGNode;
import com.simonstuck.vignelli.utils.IdGenerator;

import java.util.HashSet;
import java.util.Set;

public class ProgramDependenceGraph extends Graph<PDGNode> {

    private final Set<PsiLocalVariable> localVariables;
    private final IdGenerator<Integer> idGenerator;
    private final PDGMethodEntryNode methodEntryNode;

    /**
     * Creates a new program dependence graph using the supplied control dependence graph.
     * @param cfg The augmented control dependence graph to use
     */
    public ProgramDependenceGraph(AugmentedControlFlowGraph cfg) {
        idGenerator = new GraphNodeIdGenerator();
        localVariables = getLocalVariables(cfg);
        methodEntryNode = new PDGMethodEntryNode(idGenerator.generateId(), cfg.getMethod());
        createControlDependenciesFromEntryNode(cfg);
    }

    private Set<PsiLocalVariable> getLocalVariables(AugmentedControlFlowGraph cfg) {
        LocalVariableDeclarationCollectorVisitor variableCollector = new LocalVariableDeclarationCollectorVisitor();
        variableCollector.visitMethod(cfg.getMethod());
        return variableCollector.getLocalVariables();
    }

    private void createControlDependenciesFromEntryNode(AugmentedControlFlowGraph cfg) {
        for (CFGNode node : cfg.getNodes()) {
            processCFGNode(methodEntryNode, node, true);
        }
    }

    private void processCFGNode(PDGNode previousNode, CFGNode cfgNode, boolean controlType) {
        PDGNode pdgNode = new PDGStatementNode(idGenerator.generateId(), cfgNode, localVariables);
        addNode(pdgNode);

        PDGControlDependence controlDependence = new PDGControlDependence(previousNode, pdgNode);
        addEdge(controlDependence);
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
