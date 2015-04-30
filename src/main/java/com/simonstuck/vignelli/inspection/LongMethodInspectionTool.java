package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.controlFlow.AllVariablesControlFlowPolicy;
import com.intellij.psi.controlFlow.AnalysisCanceledException;
import com.intellij.psi.controlFlow.ControlFlow;
import com.intellij.psi.controlFlow.ControlFlowFactory;
import com.intellij.psi.controlFlow.Instruction;
import com.simonstuck.vignelli.inspection.identification.ProblemDescriptorProvider;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.psi.LineUtil;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class LongMethodInspectionTool extends ProblemReporterInspectionTool {
    private static final String OWNER_ID = "LongMethodInspectionTool";

    private static final Logger LOG = Logger.getInstance(LongMethodInspectionTool.class.getName());

    @Override
    protected Object getProblemOwner() {
        return OWNER_ID;
    }

    @Override
    protected Set<? extends ProblemDescriptorProvider> processMethodElement(PsiMethod method) {
        PsiCodeBlock body = method.getBody();
        int loc = LineUtil.countLines(body);
        int cyclomaticComplexity = getCyclomaticComplexity(method);
        PsiParameterList parameterList = method.getParameterList();
        int numParameters = parameterList.getParametersCount();
        int nestedBlockDepth = getNestedBlockDepth(method);

        LOG.debug("LOC (" + method.getName() + "): " + loc);
        LOG.debug("Cyclomatic complexity (" + method.getName() + "):" + cyclomaticComplexity);
        LOG.debug("Num Parameters (" + method.getName() + "):" + numParameters);
        LOG.debug("Nested Block Depth (" + method.getName() + "):" + nestedBlockDepth);

        double z = -11.336 + 0.598 * cyclomaticComplexity - 0.057 * loc + 4.701 * nestedBlockDepth + 0.486 * numParameters;

        double likelihood = 1 / (1 + Math.exp(-z));
        LOG.info("LIKELIHOOD (" + method.getName() + "): " + likelihood);
        return new HashSet<ProblemDescriptorProvider>();
    }

    private int getNestedBlockDepth(@NotNull PsiMethod method) {
        NestedCodeBlockVisitor nestedCodeBlockVisitor = new NestedCodeBlockVisitor();
        method.accept(nestedCodeBlockVisitor);
        return nestedCodeBlockVisitor.maxDepth;
    }

    private int getCyclomaticComplexity(@NotNull PsiMethod method) {
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

    private int getNumberOfEdges(ControlFlow flow) {
        int edges = 0;
        for (Instruction instruction : flow.getInstructions()) {
            edges += instruction.nNext();
        }
        return edges;
    }

    @Override
    protected ProblemIdentification buildProblemIdentification(ProblemDescriptor descriptor) {
        return null;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Long method";
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
