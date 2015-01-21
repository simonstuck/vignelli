package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;

public class PDGOutputDependence extends PDGDependence {
    private final PsiVariable data;

    public PDGOutputDependence(PDGNode src, PDGNode dst, PsiVariable data) {
        super(src, dst, PDGDependenceType.OUTPUT);
        this.data = data;
    }
}
