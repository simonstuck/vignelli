package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.engine.LongMethodIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.LongMethodProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LongMethodInspectionTool extends ProblemReporterInspectionTool {
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
    protected Set<? extends ProblemDescriptorProvider> processMethodElement(PsiMethod method) {
        return engine.identifyLongMethods(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(ProblemDescriptor descriptor) {
        return new LongMethodProblemIdentification(descriptor);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return LongMethodProblemIdentification.NAME;
    }
}
