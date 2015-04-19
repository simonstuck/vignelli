package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;

public interface ProblemDescriptorProvider {
    /**
     * Returns the corresponding problem descriptor for the method chain.
     * @param manager The inspection manager to use to create the problem descriptor
     * @return A new problem descriptor.
     */
    ProblemDescriptor problemDescriptor(InspectionManager manager);
}
