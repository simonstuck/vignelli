package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.decomposition.graph.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PDGNode extends GraphNode<PDGNode> implements Comparable<PDGNode> {

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

    public Set<PsiVariable> getDeclaredVariables() {
        return new HashSet<PsiVariable>(declaredVariables);
    }

    public Set<PsiVariable> getDefinedVariables() {
        return new HashSet<PsiVariable>(definedVariables);
    }

    public Set<PsiVariable> getUsedVariables() {
        return new HashSet<PsiVariable>(usedVariables);
    }

    public boolean definesLocalVariable(PsiVariable variable) {
        return definedVariables.contains(variable);
    }

    public boolean declaresLocalVariable(PsiVariable variable) {
        return declaredVariables.contains(variable);
    }

    public boolean usesLocalVariable(PsiVariable variable) {
        return usedVariables.contains(variable);
    }

    @Override
    public int hashCode() {
        //TODO: This is not too amazing
        return 37 * 17 + this.getId();
    }

    @Override
    public int compareTo(@NotNull PDGNode node) {
        if (getId() > node.getId()) {
            return 1;
        } else if (getId() < node.getId()) {
            return -1;
        } else {
            return 0;
        }
    }
}
