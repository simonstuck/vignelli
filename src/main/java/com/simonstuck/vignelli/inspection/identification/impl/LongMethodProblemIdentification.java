package com.simonstuck.vignelli.inspection.identification.impl;

import com.intellij.codeInspection.ProblemDescriptor;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.util.IOUtil;

public class LongMethodProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Long Method";

    private static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/longMethodDescription.html";

    /**
     * Creates a new {@link LongMethodProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public LongMethodProblemIdentification(ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }
}
