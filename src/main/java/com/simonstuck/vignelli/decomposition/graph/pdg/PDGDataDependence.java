package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.PsiVariable;

public class PDGDataDependence extends PDGDependence {
    private final PsiVariable data;

    public PDGDataDependence(PDGNode src, PDGNode dst, PsiVariable data) {
        super(src, dst, PDGDependenceType.DATA);
        this.data = data;
    }

    public PsiVariable getData() {
        return data;
    }
}
