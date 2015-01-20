package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.decomposition.graph.GraphNode;

import java.util.HashSet;
import java.util.Set;

public class PDGNode extends GraphNode<PDGNode> {

    protected final Set<PsiVariable> declaredVariables;
    protected final Set<PsiVariable> definedVariables;
    protected final Set<PsiVariable> usedVariables;

    /**
     * Creates a new PDGNode with the given id
     * @param id Unique id.
     */
    public PDGNode(int id) {
        super(id);
        declaredVariables = new HashSet<PsiVariable>();
        definedVariables = new HashSet<PsiVariable>();
        usedVariables = new HashSet<PsiVariable>();
    }
}
