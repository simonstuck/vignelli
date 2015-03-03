package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentificationBuilder;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MethodChainingInspectionTool extends BaseJavaLocalInspectionTool {

    private static final String METHOD_CHAIN_IDENTIFICATION_NAME = "Train Wreck";
    private static final String METHOD_CHAIN_IDENTIFICATION_DESCRIPTION_LONG = "A long description";
    private final MethodChainIdentificationEngine engine;

    private final Map<PsiMethod, Collection<ProblemIdentification>> methodProblemsMap = new HashMap<>();

    public MethodChainingInspectionTool() {
        engine = new MethodChainIdentificationEngine();
    }

    @Nullable
    @Override
    public synchronized ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        cleanMethodProblems(method.getContainingFile());

        Collection<MethodChainIdentification> methodChainIdentifications = engine.identifyMethodChains(method);
        List<ProblemDescriptor> problemDescriptors = createProblemDescriptors(manager, methodChainIdentifications);
        Collection<ProblemIdentification> identifications = buildProblemIdentifications(problemDescriptors);
        methodProblemsMap.put(method, identifications);
        notifyProblemCacheIfNecessary(manager);

        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    /**
     * Creates a list of problem descriptors for the given method chain identifications.
     * @param manager The InspectionManager to be used to create {@link com.intellij.codeInspection.ProblemDescriptor}s
     * @param methodChainIdentifications The collection of method chains
     * @return A new list of problem descriptors
     */
    private List<ProblemDescriptor> createProblemDescriptors(InspectionManager manager, Collection<MethodChainIdentification> methodChainIdentifications) {
        List<ProblemDescriptor> problemDescriptors = new ArrayList<>(methodChainIdentifications.size());
        for (MethodChainIdentification identification : methodChainIdentifications) {
            ProblemDescriptor descriptor = identification.problemDescriptor(manager);
            problemDescriptors.add(descriptor);
        }
        return problemDescriptors;
    }

    /**
     * Performs housekeeping for problems that are being tracked and saved.
     * <p>Keeps track of what methods still exist in the given file and if any have been removed,
     * the corresponding problem descriptors for any potential problems are removed.</p>
     * @param file The file for which to inspect all methods.
     */
    private void cleanMethodProblems(PsiFile file) {
        Set<PsiMethod> definedMethods = getDefinedMethods(file);
        methodProblemsMap.keySet().stream()
                .filter(problemMethod -> !definedMethods.contains(problemMethod))
                .forEach(methodProblemsMap::remove);
    }

    /**
     * Computes the set of methods that are defined in a file.
     * <p>Note that this includes methods in inner classes.</p>
     * @param file The file to search for methods
     * @return A set with all defined methods in the file
     */
    private Set<PsiMethod> getDefinedMethods(PsiFile file) {
        final Set<PsiMethod> methods = new HashSet<>();
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
        ProblemIdentificationCacheComponent cache = manager.getProject().getComponent(ProblemIdentificationCacheComponent.class);

        Collection<ProblemIdentification> allIdentifications = new LinkedList<>();
        methodProblemsMap.values().stream() .forEach(allIdentifications::addAll);

        if (!allIdentifications.isEmpty()) {
            VirtualFile file = allIdentifications.iterator().next().virtualFile();
            cache.updateFileProblems(file, allIdentifications);
        }
    }

    private Collection<ProblemIdentification> buildProblemIdentifications(Collection<ProblemDescriptor> problemDescriptors) {
        Collection<ProblemIdentification> result = new ArrayList<>();

        for (ProblemDescriptor problemDescriptor : problemDescriptors) {
            ProblemIdentification id = new ProblemIdentificationBuilder()
                    .setProblemDescriptor(problemDescriptor)
                    .setName(METHOD_CHAIN_IDENTIFICATION_NAME)
                    .setShortDescription(problemDescriptor.getDescriptionTemplate())
                    .setLongDescription(METHOD_CHAIN_IDENTIFICATION_DESCRIPTION_LONG)
                    .build();

            result.add(id);
        }
        return result;
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
