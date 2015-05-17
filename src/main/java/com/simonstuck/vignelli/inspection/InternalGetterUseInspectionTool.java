package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.engine.impl.InternalGetterUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.InternalGetterUseIdentification;
import com.simonstuck.vignelli.inspection.identification.impl.InternalGetterUseProblemIdentification;

import java.util.Set;

public class InternalGetterUseInspectionTool extends ProblemReporterInspectionTool<InternalGetterUseIdentification> {

    private static final String OWNER_ID = "InternalGetterUseInspectionTool";
    private final InternalGetterUseIdentificationEngine engine;


    public InternalGetterUseInspectionTool() {
        engine = new InternalGetterUseIdentificationEngine();
    }

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<InternalGetterUseIdentification> processMethodElement(PsiMethod method) {
        return engine.process(method);
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(InternalGetterUseIdentification candidate, ProblemDescriptor problemDescriptor) {
        return new InternalGetterUseProblemIdentification(problemDescriptor);
    }
}
