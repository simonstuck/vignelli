package com.simonstuck.vignelli.inspection.identification;

import com.google.common.base.Optional;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.controlFlow.AllVariablesControlFlowPolicy;
import com.intellij.psi.controlFlow.AnalysisCanceledException;
import com.intellij.psi.controlFlow.ControlFlow;
import com.intellij.psi.controlFlow.ControlFlowFactory;
import com.intellij.psi.controlFlow.Instruction;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.psi.LineUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LongMethodIdentificationEngine {

    private static final Logger LOG = Logger.getInstance(LongMethodIdentificationEngine.class.getName());

    //TODO: Find a sensible value for this
    private static final double LIKELIHOOD_THRESHOLD = 0.9;

    public Set<LongMethodIdentification> identifyLongMethods(PsiElement element) {
        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = PsiTreeUtil.collectElementsOfType(element, PsiMethod.class);
        Set<LongMethodIdentification> result = new HashSet<LongMethodIdentification>();
        for (PsiMethod method : methods) {
            Optional<LongMethodIdentification> id = identifyLongMethod(method);
            if (id.isPresent()) {
                result.add(id.get());
            }
        }

        return result;
    }

    private Optional<LongMethodIdentification> identifyLongMethod(PsiMethod method) {
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

        if (likelihood > LIKELIHOOD_THRESHOLD) {
            return Optional.of(new LongMethodIdentification(method));
        } else {
            return Optional.absent();
        }
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
