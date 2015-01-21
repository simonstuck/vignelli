package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;

public class PDGAntiDependence extends PDGDependence {
    private final PsiVariable data;

    public PDGAntiDependence(PDGNode src, PDGNode dst, PsiVariable data) {
        super(src, dst, PDGDependenceType.ANTI);
        this.data = data;
    }
}
