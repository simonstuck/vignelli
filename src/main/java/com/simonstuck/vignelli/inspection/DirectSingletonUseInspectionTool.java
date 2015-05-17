package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.DirectSingletonUseIdentification;
import com.simonstuck.vignelli.inspection.identification.impl.DirectSingletonUseProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DirectSingletonUseInspectionTool extends ProblemReporterInspectionTool<DirectSingletonUseIdentification> {

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
    protected Set<DirectSingletonUseIdentification> processMethodElement(PsiMethod method) {
        return engine.process(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(DirectSingletonUseIdentification candidate, ProblemDescriptor problemDescriptor) {
        return new DirectSingletonUseProblemIdentification(problemDescriptor);
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }
}
