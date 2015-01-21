package com.simonstuck.vignelli.decomposition.ast.visitor;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLocalVariable;

import java.util.HashSet;
import java.util.Set;

public class LocalVariableDeclarationCollectorVisitor extends JavaRecursiveElementVisitor {

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
