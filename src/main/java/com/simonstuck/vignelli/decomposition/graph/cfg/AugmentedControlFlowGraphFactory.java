package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssertStatement;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiBreakStatement;
import com.intellij.psi.PsiContinueStatement;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiEmptyStatement;
import com.intellij.psi.PsiExpressionListStatement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiLabeledStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiTryStatement;
import com.simonstuck.vignelli.decomposition.graph.GraphEdge;
import com.simonstuck.vignelli.decomposition.graph.GraphNodeIdGenerator;
import com.simonstuck.vignelli.utils.IdGenerator;

import java.util.HashSet;
import java.util.Set;

public class AugmentedControlFlowGraphFactory {

    private PsiMethod method;

    /**
     * Sets the method to base the control flow graph on.
     * @param method The method on which to base the CFG
     */
    public void setMethod(PsiMethod method) {
        this.method = method;
    }

    /**
     * Constructs a new augmented control flow graph from the given method.
     */
    public AugmentedControlFlowGraph makeGraph() {
        IdGenerator<Integer> nodeIdGenerator = new GraphNodeIdGenerator();
        AugmentedControlFlowGraphGeneratorVisitor visitor = new AugmentedControlFlowGraphGeneratorVisitor(nodeIdGenerator, method);
        visitor.generate();
        return visitor.getGraph();
    }

    private class AugmentedControlFlowGraphGeneratorVisitor extends JavaRecursiveElementVisitor {

        private final IdGenerator<Integer> nodeIdGenerator;
        private final PsiMethod method;
        private final AugmentedControlFlowGraph graph;

        // Initially no previous nodes, i.e. the entry node
        private final Set<CFGNode> previousNodes = new HashSet<CFGNode>();

        public AugmentedControlFlowGraphGeneratorVisitor(IdGenerator<Integer> nodeIdGenerator, PsiMethod method) {
            this.nodeIdGenerator = nodeIdGenerator;
            this.method = method;
            this.graph = new AugmentedControlFlowGraph(method);
        }

        /**
         * Generates a new augmented control flow graph from the saved method.
         * Note that this method must only be called once on the same instance.
         */
        public void generate() {
            this.visitMethod(method);
        }

        /**
         * Get the generated graph. Call this after generating the graph using generate()
         * @return The generated graph.
         */
        public AugmentedControlFlowGraph getGraph() {
            return graph;
        }

        @Override
        public void visitTryStatement(PsiTryStatement statement) {
            super.visitTryStatement(statement);
//            CFGTryNode tryNode = new CFGTryNode(nodeIdGenerator.generateId(), statement);
//            directlyNestedNodeInBlock(tryNode);
//            findBlockNodeControlParent(tryNode);
//            directlyNestedNodesInBlocks.put(tryNode, new ArrayList<CFGNode>());
//            AbstractStatement firstStatement = composite.getStatements().get(0);
//            composite = (CompositeStatementObject)firstStatement;

        }

        /**
         * Attaches the given node to the graph and adds the required edges.
         * @param node The node to attach to the graph
         */
        private void attachNodeToGraph(CFGNode node) {
            for (CFGNode previousNode : previousNodes) {
                GraphEdge<CFGNode> edge = new GraphEdge<CFGNode>(previousNode, node);
                node.addIncomingEdge(edge);
                previousNode.addOutgoingEdge(edge);
            }
            // This statement is now the new previous node
            previousNodes.clear();
            previousNodes.add(node);

            // add this new node and its associated edges to the graph
            graph.addNode(node);
            for (GraphEdge<CFGNode> edge : node.getIncomingEdges()) {
                graph.addEdge(edge);
            }
        }

        @Override
        public void visitAssertStatement(PsiAssertStatement statement) {
            CFGNode node = new CFGNode(nodeIdGenerator.generateId(), statement);
            attachNodeToGraph(node);
        }

        @Override
        public void visitDeclarationStatement(PsiDeclarationStatement statement) {
            CFGNode node = new CFGNode(nodeIdGenerator.generateId(), statement);
            attachNodeToGraph(node);
        }

        @Override
        public void visitExpressionStatement(PsiExpressionStatement statement) {
            CFGNode node = new CFGNode(nodeIdGenerator.generateId(), statement);
            attachNodeToGraph(node);
        }
    }

}
