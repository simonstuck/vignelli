package com.simonstuck.vignelli.inspection.identification;

import com.google.common.base.Optional;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.ImprovementOpportunity;
import com.simonstuck.vignelli.inspection.TrainWreckExpressionImprovementOpportunity;
import com.simonstuck.vignelli.inspection.TrainWreckVariableImprovementOpportunity;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

public class TrainWreckProblemIdentification extends ProblemIdentification {

    public static final String NAME = "Train Wreck";
    private  static final String DESCRIPTION_TEMPLATE_FILE_PATH = "descriptionTemplates/trainWreckDescription.html";

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.identification.TrainWreckProblemIdentification}.
     * <p>The new problem identification contains information about train wreck problem.</p>
     *
     * @param problemDescriptor The problem descriptor associated with the problem
     */
    public TrainWreckProblemIdentification(@NotNull ProblemDescriptor problemDescriptor) {
        super(problemDescriptor, NAME);
    }

    @Override
    public String template() {
        return IOUtils.tryReadFile(DESCRIPTION_TEMPLATE_FILE_PATH);
    }

    @Override
    public Optional<? extends ImprovementOpportunity> improvementOpportunity() {
        PsiExpressionList expressionListParent = PsiTreeUtil.getParentOfType(element, PsiExpressionList.class);
        PsiLocalVariable varParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);

        if (expressionListParent != null) {
            // check for the expression list first as a variable assignment can exist higher up the tree.
            return Optional.of(new TrainWreckExpressionImprovementOpportunity(element));
        } else if (varParent != null) {
            return Optional.of(new TrainWreckVariableImprovementOpportunity(element,varParent));
        } else {
            return Optional.of(new TrainWreckExpressionImprovementOpportunity(element));
        }
    }
}
