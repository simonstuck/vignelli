package com.simonstuck.vignelli.decomposition.graph.cfg;

import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.decomposition.graph.GraphNode;
import org.jetbrains.annotations.NotNull;

public class CFGNode extends GraphNode implements Comparable<CFGNode> {

    protected final PsiStatement statement;

    public CFGNode(int id, @NotNull PsiStatement statement) {
        super(id);
        this.statement = statement;
    }

    @Override
    public int hashCode() {
        //TODO: This is not too amazing
        return 37 * 17 + this.getId();
    }

    @Override
    public int compareTo(@NotNull CFGNode node) {
        if (getId() > node.getId()) {
            return 1;
        } else if (getId() < node.getId()) {
            return -1;
        } else {
            return 0;
        }
    }
}
