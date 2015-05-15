package com.simonstuck.vignelli.inspection.identification.impl;

import com.google.common.base.Optional;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.inspection.improvement.impl.DirectSingletonUseImprovementOpportunity;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

public class InternalGetterUseProblemIdentification extends ProblemIdentification {
    public static final String NAME = "Internal Use of a Getter";
    private static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/internalGetterUseDescription.html";

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.identification.impl.InternalGetterUseProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public InternalGetterUseProblemIdentification(@NotNull ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public Optional<? extends ImprovementOpportunity> improvementOpportunity() {
        return Optional.absent();
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }
}
