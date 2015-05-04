package com.simonstuck.vignelli.inspection.identification.impl;

import com.google.common.base.Optional;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.inspection.improvement.impl.DirectSingletonUseImprovementOpportunity;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

public class DirectSingletonUseProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Direct Use of Singleton";
    private static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/directSingletonUseDescription.html";

    /**
     * Creates a new {@link DirectSingletonUseProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public DirectSingletonUseProblemIdentification(@NotNull ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }

    @Override
    public Optional<? extends ImprovementOpportunity> improvementOpportunity() {
        PsiMethodCallExpression getInstanceCall = (PsiMethodCallExpression) problemDescriptor.getPsiElement();
        return Optional.of(new DirectSingletonUseImprovementOpportunity(getInstanceCall));
    }
}
