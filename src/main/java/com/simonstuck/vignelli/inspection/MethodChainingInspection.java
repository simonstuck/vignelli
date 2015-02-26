package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentificationBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodChainingInspection extends BaseJavaLocalInspectionTool {

    private static final String METHOD_CHAIN_IDENTIFICATION_NAME = "Train Wreck";
    private static final String METHOD_CHAIN_IDENTIFICATION_DESCRIPTION_LONG = "A long description";
    private final MethodChainIdentificationEngine engine;

    private final Map<PsiMethod, Collection<ProblemDescriptor>> methodProblemsMap = new HashMap<PsiMethod, Collection<ProblemDescriptor>>();

    public MethodChainingInspection() {
        engine = new MethodChainIdentificationEngine();
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        MethodChainIdentificationCollection methodChainIdentifications = engine.identifyMethodChains(method);
        List<ProblemDescriptor> problemDescriptors = new ArrayList<ProblemDescriptor>(methodChainIdentifications.size());

        cleanMethodProblems(method.getContainingFile());

        for (MethodChainIdentification identification : methodChainIdentifications) {
            ProblemDescriptor descriptor = getProblemDescriptor(manager, identification);
            problemDescriptors.add(descriptor);
        }

        methodProblemsMap.put(method, problemDescriptors);
        notifyProblemCacheIfNecessary(manager);


        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    private ProblemDescriptor getProblemDescriptor(InspectionManager manager, MethodChainIdentification identification) {
        String message = identification.shortDescription();
        PsiElement start = identification.getFirstCall();
        PsiElement end = identification.getFinalCall();
        return manager.createProblemDescriptor(start, end, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
    }

    private void cleanMethodProblems(PsiFile file) {
        Set<PsiMethod> definedMethods = getDefinedMethods(file);
        for (PsiMethod problemMethod : methodProblemsMap.keySet()) {
            if (!definedMethods.contains(problemMethod)) {
                methodProblemsMap.remove(problemMethod);
            }
        }
    }

    private Set<PsiMethod> getDefinedMethods(PsiFile file) {
        final Set<PsiMethod> methods = new HashSet<PsiMethod>();
        JavaRecursiveElementVisitor methodFinder = new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                methods.add(method);
            }
        };
        file.accept(methodFinder);
        return methods;
    }

    private void notifyProblemCacheIfNecessary(InspectionManager manager) {
        ProblemIdentificationCache cache = manager.getProject().getComponent(ProblemIdentificationCache.class);
        IdentificationCollection<ProblemIdentification> result = new IdentificationCollection<ProblemIdentification>();

        for (Collection<ProblemDescriptor> problemDescriptors : methodProblemsMap.values()) {
            for (ProblemDescriptor problemDescriptor : problemDescriptors) {
                ProblemIdentification id = new ProblemIdentificationBuilder()
                        .setProblemDescriptor(problemDescriptor)
                        .setName(METHOD_CHAIN_IDENTIFICATION_NAME)
                        .setShortDescription(problemDescriptor.getDescriptionTemplate())
                        .setLongDescription(METHOD_CHAIN_IDENTIFICATION_DESCRIPTION_LONG)
                        .build();

                result.add(id);
            }
        }

        if (result.size() > 0) {
            VirtualFile file = result.iterator().next().getProblemDescriptor().getPsiElement().getContainingFile().getVirtualFile();
            cache.updateFileProblems(file, result);
        }
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Train Wreck";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
