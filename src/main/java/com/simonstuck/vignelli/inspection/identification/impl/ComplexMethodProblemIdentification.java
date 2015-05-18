package com.simonstuck.vignelli.inspection.identification.impl;

import com.intellij.codeInspection.ProblemDescriptor;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.util.IOUtil;

public class ComplexMethodProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Complex Method";

    private static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/complexMethodDescription.html";

    /**
     * Creates a new {@link ComplexMethodProblemIdentification}.
     * <p>The new problem identification contains information about method complexity.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public ComplexMethodProblemIdentification(ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }
}
