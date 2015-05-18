package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.impl.ComplexMethodIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.ComplexMethodIdentification;
import com.simonstuck.vignelli.inspection.identification.impl.ComplexMethodProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ComplexMethodInspectionTool extends ProblemReporterInspectionTool<ComplexMethodIdentification> {
    private static final String OWNER_ID = "ComplexMethodInspectionTool";
    private final ComplexMethodIdentificationEngine engine;

    public ComplexMethodInspectionTool() {
        engine = new ComplexMethodIdentificationEngine();
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<ComplexMethodIdentification> processMethodElement(PsiMethod method) {
        return engine.process(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(ComplexMethodIdentification candidate, ProblemDescriptor descriptor) {
        return new ComplexMethodProblemIdentification(descriptor);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return ComplexMethodProblemIdentification.NAME;
    }
}
