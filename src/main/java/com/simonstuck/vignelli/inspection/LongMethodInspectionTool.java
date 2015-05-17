package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.impl.LongMethodIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.LongMethodIdentification;
import com.simonstuck.vignelli.inspection.identification.impl.LongMethodProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LongMethodInspectionTool extends ProblemReporterInspectionTool<LongMethodIdentification> {
    private static final String OWNER_ID = "LongMethodInspectionTool";
    private final LongMethodIdentificationEngine engine;

    public LongMethodInspectionTool() {
        engine = new LongMethodIdentificationEngine();
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<LongMethodIdentification> processMethodElement(PsiMethod method) {
        return engine.process(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(LongMethodIdentification candidate, ProblemDescriptor descriptor) {
        return new LongMethodProblemIdentification(descriptor);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return LongMethodProblemIdentification.NAME;
    }
}
