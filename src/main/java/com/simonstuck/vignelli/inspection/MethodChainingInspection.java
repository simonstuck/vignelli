package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationEngine;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MethodChainingInspection extends BaseJavaLocalInspectionTool {

    private final MethodChainIdentificationEngine engine;

    public MethodChainingInspection() {
        engine = new MethodChainIdentificationEngine();
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        MethodChainIdentificationCollection methodChainIdentifications = engine.identifyMethodChains(method);
        List<ProblemDescriptor> problemDescriptors = new ArrayList<ProblemDescriptor>(methodChainIdentifications.size());

        for (MethodChainIdentification identification : methodChainIdentifications) {
            String message = identification.getShortDescription();

            PsiElement start = identification.getFirstCall();
            PsiElement end = identification.getFinalCall();
            ProblemDescriptor descriptor = manager.createProblemDescriptor(start, end, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
            problemDescriptors.add(descriptor);
        }
        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Train Wreck";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
