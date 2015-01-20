package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiMethod;

public class PDGMethodEntryNode extends PDGNode {
    private final PsiMethod method;

    public PDGMethodEntryNode(int id, PsiMethod method) {
        super(id);
        this.method = method;
    }
}
