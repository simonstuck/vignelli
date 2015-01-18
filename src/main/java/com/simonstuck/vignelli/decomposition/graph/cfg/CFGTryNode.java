package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.intellij.psi.PsiTryStatement;

public class CFGTryNode extends CFGBlockNode {
    public CFGTryNode(int id, PsiTryStatement statement) {
        super(id, statement);
    }
}
