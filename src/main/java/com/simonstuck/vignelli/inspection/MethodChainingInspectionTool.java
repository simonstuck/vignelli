package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.TrainWreckIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckProblemIdentification;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MethodChainingInspectionTool extends ProblemReporterInspectionTool<TrainWreckIdentification> {

    private static final String OWNER_ID = "MethodChainingInspectionTool";

    private TrainWreckIdentificationEngine getIdentificationEngine(Project project) {
        //TODO: Measure performance here, this could be a bottleneck!
        return new TrainWreckIdentificationEngine(new IntelliJClassFinderAdapter(project));
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<TrainWreckIdentification> processMethodElement(PsiMethod method) {
        return getIdentificationEngine(method.getProject()).process(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(TrainWreckIdentification candidate, ProblemDescriptor problemDescriptor) {
        return new TrainWreckProblemIdentification(candidate, problemDescriptor);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return TrainWreckProblemIdentification.NAME;
    }
}
