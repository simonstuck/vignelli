package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseIdentification;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseIdentificationEngine;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DirectSingletonUseInspectionTool extends BaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance(DirectSingletonUseInspectionTool.class.getName());

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(final @NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        LOG.debug("checkMethod");
        DirectSingletonUseIdentificationEngine engine = new DirectSingletonUseIdentificationEngine();
        Set<DirectSingletonUseIdentification> uses = engine.process(method);
        return constructProblemDescriptors(manager, uses);
    }

    private ProblemDescriptor[] constructProblemDescriptors(InspectionManager manager, Set<DirectSingletonUseIdentification> uses) {
        List<ProblemDescriptor> descriptors = new LinkedList<ProblemDescriptor>();
        for (DirectSingletonUseIdentification id : uses) {
            descriptors.add(id.problemDescriptor(manager));
        }
        return descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Direct Use of Singleton";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
