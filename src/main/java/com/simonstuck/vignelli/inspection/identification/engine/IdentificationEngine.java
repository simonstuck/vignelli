package com.simonstuck.vignelli.inspection.identification.engine;

import com.intellij.psi.PsiElement;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;

import java.util.Set;

public interface IdentificationEngine<T extends ProblemDescriptorProvider> {
    /**
     * Process the given element to find problems.
     * @param element The element to process
     * @return A set of identifications that are able to generate problem descriptors.
     */
    Set<T> process(PsiElement element);
}
