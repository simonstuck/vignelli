package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.TrainWreckProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MethodChainingInspectionTool extends ProblemReporterInspectionTool {

    private static final String OWNER_ID = "MethodChainingInspectionTool";

    private MethodChainIdentificationEngine getIdentificationEngine(Project project) {
        //TODO: Measure performance here, this could be a bottleneck!
        return new MethodChainIdentificationEngine(new IntelliJClassFinderAdapter(project));
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<? extends ProblemDescriptorProvider> processMethodElement(PsiMethod method) {
        return getIdentificationEngine(method.getProject()).identifyMethodChains(method);
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
