package com.simonstuck.vignelli.inspection.identification;

import com.google.common.base.Optional;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.DirectSingletonUseImprovementOpportunity;
import com.simonstuck.vignelli.inspection.ImprovementOpportunity;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

public class DirectSingletonUseProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Direct Use of Singleton";
    private static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/directSingletonUseDescription.html";

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

    @Override
    public Optional<? extends ImprovementOpportunity> improvementOpportunity() {
        PsiMethodCallExpression getInstanceCall = (PsiMethodCallExpression) problemDescriptor.getPsiElement();
        return Optional.of(new DirectSingletonUseImprovementOpportunity(getInstanceCall));
    }
}
