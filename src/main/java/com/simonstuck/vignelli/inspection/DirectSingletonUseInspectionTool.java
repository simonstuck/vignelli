package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DirectSingletonUseInspectionTool extends ProblemReporterInspectionTool {

    private static final String OWNER_ID = "DirectSingletonUseInspectionTool";

    private final DirectSingletonUseIdentificationEngine engine;

    public DirectSingletonUseInspectionTool() {
        engine = new DirectSingletonUseIdentificationEngine();
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return DirectSingletonUseProblemIdentification.NAME;
    }

    @Override
    protected Set<? extends ProblemDescriptorProvider> processMethodElement(PsiMethod method) {
        return engine.process(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(ProblemDescriptor descriptor) {
        return new DirectSingletonUseProblemIdentification(descriptor);
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }
}
