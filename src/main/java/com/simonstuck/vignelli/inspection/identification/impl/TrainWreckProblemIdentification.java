package com.simonstuck.vignelli.inspection.identification.impl;

import com.google.common.base.Optional;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.improvement.ImprovementOpportunity;
import com.simonstuck.vignelli.inspection.improvement.impl.TrainWreckExpressionImprovementOpportunity;
import com.simonstuck.vignelli.inspection.improvement.impl.TrainWreckVariableImprovementOpportunity;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

public class TrainWreckProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Train Wreck";
    private  static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/trainWreckDescription.html";

    @NotNull
    private final TrainWreckIdentification trainWreckCandidate;

    /**
     * Creates a new {@link TrainWreckProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param trainWreckCandidate The train wreck candidate
     * @param problemDescriptor The problem descriptor associated with this problem identification
     */
    public TrainWreckProblemIdentification(@NotNull TrainWreckIdentification trainWreckCandidate, @NotNull ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
        this.trainWreckCandidate = trainWreckCandidate;
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }

    @Override
    public Optional<? extends ImprovementOpportunity> improvementOpportunity() {
        PsiExpressionList expressionListParent = PsiTreeUtil.getParentOfType(element, PsiExpressionList.class);
        PsiLocalVariable varParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);

        if (expressionListParent != null) {
            // check for the expression list first as a variable assignment can exist higher up the tree.
            return Optional.of(new TrainWreckExpressionImprovementOpportunity(trainWreckCandidate));
        } else if (varParent != null) {
            return Optional.of(new TrainWreckVariableImprovementOpportunity(trainWreckCandidate,varParent));
        } else {
            return Optional.of(new TrainWreckExpressionImprovementOpportunity(trainWreckCandidate));
        }
    }
}
