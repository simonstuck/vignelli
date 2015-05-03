package com.simonstuck.vignelli.utils;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.controlFlow.AllVariablesControlFlowPolicy;
import com.intellij.psi.controlFlow.AnalysisCanceledException;
import com.intellij.psi.controlFlow.ControlFlow;
import com.intellij.psi.controlFlow.ControlFlowFactory;
import com.intellij.psi.controlFlow.Instruction;

import org.jetbrains.annotations.NotNull;

public class MetricsUtil {
    /**
     * Finds the maximum nested block depth of the given method.
     * @param method The method for which to find the nested block depth.
     * @return The maximum nested block depth.
     */
    public static int getNestedBlockDepth(@NotNull PsiMethod method) {
        NestedCodeBlockVisitor nestedCodeBlockVisitor = new NestedCodeBlockVisitor();
        method.accept(nestedCodeBlockVisitor);
        return nestedCodeBlockVisitor.maxDepth;
    }

    /**
     * Calculates the cyclomatic complexity of the given method.
     * @param method The method to check.
     * @return The cyclomatic complexity of the given method.
     */
    public static int getCyclomaticComplexity(@NotNull PsiMethod method) {
        /*
         * M = Cyclomatic Complexity
         * E = Edges of the graph
         * N = Nodes of the graph
         * M = E - N + 2
         */
        if (!method.isValid() || method.getBody() == null) {
            return -1;
        }
        try {
            ControlFlowFactory controlFlowFactory = ControlFlowFactory.getInstance(method.getProject());
            ControlFlow flow = controlFlowFactory.getControlFlow(method.getBody(), new AllVariablesControlFlowPolicy());

            int nodes = flow.getSize();
            int edges = getNumberOfEdges(flow);

            return edges - nodes + 2;
        } catch (AnalysisCanceledException ignored) {}

        return -1;
    }

    /**
     * Gets the number of edges in the given {@link com.intellij.psi.controlFlow.ControlFlow}
     * @param flow The control flow to check.
     * @return The number of edges in the control flow.
     */
    public static int getNumberOfEdges(ControlFlow flow) {
        int edges = 0;
        for (Instruction instruction : flow.getInstructions()) {
            edges += instruction.nNext();
        }
        return edges;
    }

    private static class NestedCodeBlockVisitor extends JavaRecursiveElementVisitor {
        private int maxDepth = 0;
        private int currentDepth = 0;

        @Override
        public void visitCodeBlock(PsiCodeBlock block) {
            currentDepth++;
            super.visitCodeBlock(block);
            if (currentDepth > maxDepth) {
                maxDepth = currentDepth;
            }
            currentDepth--;
        }
    }
}
