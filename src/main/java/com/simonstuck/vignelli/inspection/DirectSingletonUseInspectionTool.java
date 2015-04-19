package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseIdentification;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.DirectSingletonUseProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirectSingletonUseInspectionTool extends BaseJavaLocalInspectionTool {

    private final DirectSingletonUseIdentificationEngine engine;

    private final Map<PsiMethod, Collection<ProblemIdentification>> methodProblemsMap = new HashMap<PsiMethod, Collection<ProblemIdentification>>();

    public DirectSingletonUseInspectionTool() {
        engine = new DirectSingletonUseIdentificationEngine();
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(final @NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        Set<DirectSingletonUseIdentification> uses = engine.process(method);
        List<ProblemDescriptor> problemDescriptors = constructProblemDescriptors(manager,uses);
        Collection<ProblemIdentification> identifications = buildProblemIdentifications(problemDescriptors);
        methodProblemsMap.put(method, identifications);
        notifyProblemCacheIfNecessary(method.getContainingFile().getVirtualFile(), manager);
        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    private List<ProblemDescriptor> constructProblemDescriptors(InspectionManager manager, Set<DirectSingletonUseIdentification> uses) {
        List<ProblemDescriptor> descriptors = new LinkedList<ProblemDescriptor>();
        for (DirectSingletonUseIdentification id : uses) {
            descriptors.add(id.problemDescriptor(manager));
        }
        return descriptors;
    }

    private void notifyProblemCacheIfNecessary(VirtualFile virtualFile, InspectionManager manager) {
        ProblemIdentificationCacheComponent cache = manager.getProject().getComponent(ProblemIdentificationCacheComponent.class);

        Collection<ProblemIdentification> allIdentifications = new LinkedList<ProblemIdentification>();
        for (Collection<ProblemIdentification> identifications : methodProblemsMap.values()) {
            allIdentifications.addAll(identifications);
        }

        cache.updateFileProblems(virtualFile, this, allIdentifications);
    }

    private Collection<ProblemIdentification> buildProblemIdentifications(Collection<ProblemDescriptor> problemDescriptors) {
        List<ProblemIdentification> result = new ArrayList<ProblemIdentification>();
        for (ProblemDescriptor descriptor : problemDescriptors) {
            result.add(new DirectSingletonUseProblemIdentification(descriptor));
        }
        return result;
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
