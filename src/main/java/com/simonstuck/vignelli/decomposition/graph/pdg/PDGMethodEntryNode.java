package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

public class PDGMethodEntryNode extends PDGNode {
    private final PsiMethod method;

    public PDGMethodEntryNode(int id, PsiMethod method) {
        super(id);
        this.method = method;

        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            declaredVariables.add(parameter);
            definedVariables.add(parameter);
        }
    }
}
