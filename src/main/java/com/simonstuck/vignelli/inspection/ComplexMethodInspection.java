package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComplexMethodInspection extends BaseJavaLocalInspectionTool {

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        String message = "This method appears to be very complex. You may wish to extract some of its logic into other methods.";
        PsiIdentifier nameId = method.getNameIdentifier();
        TextRange highlightRange = new TextRange(nameId.getStartOffsetInParent(), nameId.getStartOffsetInParent() + nameId.getTextLength());
        ProblemDescriptor descriptor = new ProblemDescriptorImpl(method, method, message, null,
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false, highlightRange, true);

        return new ProblemDescriptor[] { descriptor };
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Complex Method";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
