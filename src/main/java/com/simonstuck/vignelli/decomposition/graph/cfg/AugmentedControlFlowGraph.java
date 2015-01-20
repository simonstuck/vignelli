package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.decomposition.graph.Graph;

/**
 * The augmented control flow graph for a method is the modified abstract syntax tree that
 * represents all branching statements as pseudo-predicates.
 * Pseudo predicate nodes has two outgoing edges:
 * - the one labelled 'true' goes to the target of the jump
 * - the one labelled 'false' goes to the statement following the branching statement
 *   if no branching occurred.
 */
public class AugmentedControlFlowGraph extends Graph<CFGNode> {

    private final PsiMethod method;

    /**
     * Constructs a new augmented control flow graph from the given method.
     * @param method The method to base the CFG on
     */
    public AugmentedControlFlowGraph(PsiMethod method) {
        this.method = method;
    }

    /**
     * Gets the method that this control flow graph is built on.
     * @return The method that this graph is built from.
     */
    public PsiMethod getMethod() {
        return method;
    }
}
