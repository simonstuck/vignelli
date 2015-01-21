package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.decomposition.ast.util.PsiMethodUtil;
import com.simonstuck.vignelli.decomposition.graph.cfg.AugmentedControlFlowGraph;
import com.simonstuck.vignelli.decomposition.graph.cfg.AugmentedControlFlowGraphFactory;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlockCollection;
import com.simonstuck.vignelli.decomposition.graph.pdg.PDGSliceUnionCollection;
import com.simonstuck.vignelli.decomposition.graph.pdg.ProgramDependenceGraph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ComplexMethodInspection extends BaseJavaLocalInspectionTool {

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        String message = "This method appears to be very complex. You may wish to extract some of its logic into other methods.";
        PsiIdentifier nameId = method.getNameIdentifier();
        TextRange highlightRange = new TextRange(nameId.getStartOffsetInParent(), nameId.getStartOffsetInParent() + nameId.getTextLength());
        ProblemDescriptor descriptor = new ProblemDescriptorImpl(method, method, message, null,
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false, highlightRange, true);

        performInspection(method);

        return new ProblemDescriptor[] { descriptor };
    }

    private void performInspection(PsiMethod method) {
        AugmentedControlFlowGraphFactory factory = new AugmentedControlFlowGraphFactory();
        factory.setMethod(method);
        AugmentedControlFlowGraph graph = factory.makeGraph();
        BasicBlockCollection basicBlocks = new BasicBlockCollection(graph);
        System.out.println("Analysing method: " + method.getName());
        System.out.println(graph);
        System.out.println("basic blocks:");
        System.out.println(basicBlocks);
        ProgramDependenceGraph pdg = new ProgramDependenceGraph(graph, basicBlocks);
        System.out.println("\nProgram dependence graph:");
        System.out.println(pdg);


        // 1) Identify local variables in method
        Set<PsiLocalVariable> localVariables = PsiMethodUtil.getLocalVariables(method);

        // 2) for each variable v, identify seed statements C which contain an assignment of variable v.
        // These statements along with variable v form a set of slicing critera (c, v) where c is in C.

        // 3) for each statement c in C compute the set of boundary blocks

        // 4) Calculate the common boundary blocks for the statements in set C as the intersection
        // of all boundary blocks

        // 5) For each slicing criterion (c,v) where c in C and boundary block B_n in boundary
        // blocks of C compute the block based slice S(c,v,B_n). This is the set of statements
        // that may affect the computation of variable v at statement c (backward slice, extracted
        // from the PDG subgraph corresponding to block-based region R(B_n).

        // 6) For each B_n in boundary blocks of C the union of slices is a slice that covers the
        // complete computation of variable v within region R(B_n).

        for (PsiLocalVariable localVariable : localVariables) {
            PDGSliceUnionCollection collection = new PDGSliceUnionCollection(pdg, localVariable);
            System.out.println("Found a collection: \n\n");
            System.out.println(collection);
            System.out.println("\n------------------");
        }

    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Complex Method";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
