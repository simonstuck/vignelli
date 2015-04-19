package com.simonstuck.vignelli.inspection.identification;

import com.intellij.codeInspection.ProblemDescriptor;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

public class DirectSingletonUseProblemIdentification extends ProblemIdentification {

    private static final String NAME = "Direct Use of Singleton";
    public static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/directSingletonUseDescription.html";

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.identification.DirectSingletonUseProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public DirectSingletonUseProblemIdentification(@NotNull ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public String template() {
        return IOUtils.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }
}
