package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

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

abstract class ProblemReporterInspectionTool<T extends ProblemDescriptorProvider> extends BaseJavaLocalInspectionTool {
    private final Map<PsiMethod, Collection<ProblemIdentification>> methodProblemsMap = new HashMap<PsiMethod, Collection<ProblemIdentification>>();

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
    public ProblemDescriptor[] checkMethod(final @NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        cleanMethodProblems(method.getContainingFile());

        Set<T> uses = processMethodElement(method);
        List<Pair<T, ProblemDescriptor>> candidatesWithProblemDescriptors = createProblemDescriptors(manager, uses);
        Collection<ProblemIdentification> identifications = buildProblemIdentifications(candidatesWithProblemDescriptors);
        methodProblemsMap.put(method, identifications);
        notifyProblemCacheIfNecessary(method.getContainingFile().getVirtualFile(), manager);

        List<ProblemDescriptor> problemDescriptors = getProblemDescriptors(candidatesWithProblemDescriptors);

        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    private List<ProblemDescriptor> getProblemDescriptors(List<Pair<T, ProblemDescriptor>> candidatesWithProblemDescriptors) {
        List<ProblemDescriptor> problemDescriptors = new LinkedList<ProblemDescriptor>();
        for (Pair<T, ProblemDescriptor> candidate : candidatesWithProblemDescriptors) {
            problemDescriptors.add(candidate.getSecond());
        }
        return problemDescriptors;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    /**
     * Creates a list of problem descriptors for the given method chain identifications.
     * @param manager The InspectionManager to be used to create {@link com.intellij.codeInspection.ProblemDescriptor}s
     * @param problems The collection of problems
     * @return A new list of problem descriptors
     */
    private List<Pair<T, ProblemDescriptor>> createProblemDescriptors(InspectionManager manager, Set<T> problems) {
        List<Pair<T, ProblemDescriptor>> result = new LinkedList<Pair<T, ProblemDescriptor>>();
        for (T id : problems) {
            result.add(Pair.create(id, id.problemDescriptor(manager)));
        }
        return result;
    }

    /**
     * Builds problem identifications using for the given problem {@link com.intellij.codeInspection.ProblemDescriptor} instances.
     * @param candidates The problem candidates from which to construct the problem identifications
     * @return A new collection of problem identifications
     */
    private Collection<ProblemIdentification> buildProblemIdentifications(List<Pair<T, ProblemDescriptor>> candidates) {
        List<ProblemIdentification> result = new ArrayList<ProblemIdentification>();
        for (Pair<T, ProblemDescriptor> candidate : candidates) {
            result.add(buildProblemIdentification(candidate.getFirst(), candidate.getSecond()));
        }
        return result;
    }

    /**
     * Notifies the global problem cache with the current problems in this file.
     * @param virtualFile The current file
     * @param manager The {@link com.intellij.codeInspection.InspectionManager} to use
     */
    private void notifyProblemCacheIfNecessary(VirtualFile virtualFile, InspectionManager manager) {
        ProblemIdentificationCacheComponent cache = manager.getProject().getComponent(ProblemIdentificationCacheComponent.class);
        if (cache != null) {
            Collection<ProblemIdentification> allIdentifications = new LinkedList<ProblemIdentification>();
            for (Collection<ProblemIdentification> identifications : methodProblemsMap.values()) {
                allIdentifications.addAll(identifications);
            }

            cache.updateFileProblems(virtualFile, getProblemOwner(), allIdentifications);
        }
    }

    /**
     * Gets the owner of the problems that this inspection tool generates.
     * <p>Note that this cannot be <code>this</code> as the inspection tool may be instantiated multiple times.</p>
     * @return The problem owner that remains active throughout the lifetime of the plugin instance.
     */
    protected abstract Object getProblemOwner();

    /**
     * Processes a single method to retrieve {@link com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider}s.
     * @param method The method to process.
     * @return A set of {@link com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider}.
     */
    protected abstract Set<T> processMethodElement(PsiMethod method);

    /**
     * Builds a single {@link com.simonstuck.vignelli.inspection.identification.ProblemIdentification}.
     * @param descriptor The descriptor for the problem given by the inspection tool.
     * @param problemDescriptor The problem descriptor for this candidate
     * @return The new problem identification.
     */
    protected abstract ProblemIdentification buildProblemIdentification(T descriptor, ProblemDescriptor problemDescriptor);

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
}
