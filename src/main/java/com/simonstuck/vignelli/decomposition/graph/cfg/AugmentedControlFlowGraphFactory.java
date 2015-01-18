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
import com.simonstuck.vignelli.decomposition.graph.GraphNode;
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
        private final Set<GraphNode> previousNodes = new HashSet<GraphNode>();

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
        private void attachNodeToGraph(GraphNode node) {
            for (GraphNode previousNode : previousNodes) {
                node.addIncomingEdge(new GraphEdge(previousNode, node));
            }
            // This statement is now the new previous node
            previousNodes.clear();
            previousNodes.add(node);

            // add this new node and its associated edges to the graph
            graph.addNode(node);
            for (GraphEdge edge : node.getIncomingEdges()) {
                graph.addEdge(edge);
            }
        }

        @Override
        public void visitAssertStatement(PsiAssertStatement statement) {
            CFGNode node = new CFGNode(nodeIdGenerator.generateId(), statement);
            attachNodeToGraph(node);
        }

        @Override
        public void visitBlockStatement(PsiBlockStatement statement) {
            super.visitBlockStatement(statement);
        }

        @Override
        public void visitBreakStatement(PsiBreakStatement statement) {
            super.visitBreakStatement(statement);
        }

        @Override
        public void visitContinueStatement(PsiContinueStatement statement) {
            super.visitContinueStatement(statement);
        }

        @Override
        public void visitDeclarationStatement(PsiDeclarationStatement statement) {
            CFGNode node = new CFGNode(nodeIdGenerator.generateId(), statement);
            attachNodeToGraph(node);
        }

        @Override
        public void visitEmptyStatement(PsiEmptyStatement statement) {
            // ignore Empty statement
        }

        @Override
        public void visitExpressionListStatement(PsiExpressionListStatement statement) {
            super.visitExpressionListStatement(statement);
        }

        @Override
        public void visitExpressionStatement(PsiExpressionStatement statement) {
            CFGNode node = new CFGNode(nodeIdGenerator.generateId(), statement);
            attachNodeToGraph(node);
        }

        @Override
        public void visitLabeledStatement(PsiLabeledStatement statement) {
            super.visitLabeledStatement(statement);
        }

        @Override
        public void visitReturnStatement(PsiReturnStatement statement) {
            super.visitReturnStatement(statement);
        }
    }

}
