package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.ProblemDescriptor;
import com.simonstuck.vignelli.utils.IOUtils;

public class LongMethodProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Long Method";

    private static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/longMethodDescription.html";

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.identification.LongMethodProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public LongMethodProblemIdentification(ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public String template() {
        return IOUtils.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }
}
