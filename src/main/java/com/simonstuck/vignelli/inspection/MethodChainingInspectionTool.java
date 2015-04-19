package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentification;
import com.simonstuck.vignelli.inspection.identification.MethodChainIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.inspection.identification.TrainWreckProblemIdentification;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodChainingInspectionTool extends BaseJavaLocalInspectionTool {

    private final MethodChainIdentificationEngine engine;

    private final Map<PsiMethod, Collection<ProblemIdentification>> methodProblemsMap = new HashMap<PsiMethod, Collection<ProblemIdentification>>();

    public MethodChainingInspectionTool() {
        engine = new MethodChainIdentificationEngine();
    }

    @Nullable
    @Override
    public synchronized ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ProblemDescriptor[] result = super.checkFile(file, manager, isOnTheFly);
        // If there are no more methods defined, we have to remove any remaining problems that
        // may still be cached.
        if (getDefinedMethods(file).isEmpty()) {
            cleanMethodProblems(file);
            notifyProblemCacheIfNecessary(file.getVirtualFile(), manager);
        }
        return result;
    }

    @Nullable
    @Override
    public synchronized ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        cleanMethodProblems(method.getContainingFile());

        Collection<MethodChainIdentification> methodChainIdentifications = engine.identifyMethodChains(method);
        List<ProblemDescriptor> problemDescriptors = createProblemDescriptors(manager, methodChainIdentifications);
        Collection<ProblemIdentification> identifications = buildProblemIdentifications(problemDescriptors);
        methodProblemsMap.put(method, identifications);
        notifyProblemCacheIfNecessary(method.getContainingFile().getVirtualFile(), manager);

        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    /**
     * Creates a list of problem descriptors for the given method chain identifications.
     * @param manager The InspectionManager to be used to create {@link com.intellij.codeInspection.ProblemDescriptor}s
     * @param methodChainIdentifications The collection of method chains
     * @return A new list of problem descriptors
     */
    private List<ProblemDescriptor> createProblemDescriptors(InspectionManager manager, Collection<MethodChainIdentification> methodChainIdentifications) {
        List<ProblemDescriptor> problemDescriptors = new ArrayList<ProblemDescriptor>(methodChainIdentifications.size());
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
    private synchronized void cleanMethodProblems(PsiFile file) {
        final Set<PsiMethod> definedMethods = getDefinedMethods(file);
        Collection<PsiMethod> toRemove = new HashSet<PsiMethod>();
        for (PsiMethod method : methodProblemsMap.keySet()) {
            if (!definedMethods.contains(method)) {
                toRemove.add(method);
            }
        }
        for (PsiMethod method : toRemove) {
            methodProblemsMap.remove(method);
        }
    }

    /**
     * Computes the set of methods that are defined in a file.
     * <p>Note that this includes methods in inner classes.</p>
     * @param file The file to search for methods
     * @return A set with all defined methods in the file
     */
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
            result.add(new TrainWreckProblemIdentification(descriptor));
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
