package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.engine.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.TrainWreckProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MethodChainingInspectionTool extends ProblemReporterInspectionTool {

    private final MethodChainIdentificationEngine engine;
    private static final String OWNER_ID = "MethodChainingInspectionTool";

    public MethodChainingInspectionTool() {
        engine = new MethodChainIdentificationEngine();
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<? extends ProblemDescriptorProvider> processMethodElement(PsiMethod method) {
        return engine.identifyMethodChains(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(ProblemDescriptor descriptor) {
        return new TrainWreckProblemIdentification(descriptor);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return TrainWreckProblemIdentification.NAME;
    }
}
