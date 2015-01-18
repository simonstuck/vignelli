package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.intellij.psi.PsiStatement;

public class CFGBlockNode extends CFGNode {
    public CFGBlockNode(int id, PsiStatement statement) {
        super(id, statement);
    }
}
