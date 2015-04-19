package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseIdentificationEngine;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectSingletonUseInspectionTool extends BaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance(DirectSingletonUseInspectionTool.class.getName());

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(final PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        LOG.debug("checkMethod");
        DirectSingletonUseIdentificationEngine engine = new DirectSingletonUseIdentificationEngine();
        engine.process(method);
        return super.checkMethod(method, manager, isOnTheFly);
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
