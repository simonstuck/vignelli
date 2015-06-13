package com.simonstuck.vignelli.inspection.identification.engine.impl;

import com.google.common.base.Optional;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.inspection.identification.engine.IdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.ComplexMethodIdentification;
import com.simonstuck.vignelli.psi.util.LineUtil;
import com.simonstuck.vignelli.psi.util.MetricsUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ComplexMethodIdentificationEngine implements IdentificationEngine<ComplexMethodIdentification> {

    private static final Logger LOG = Logger.getInstance(ComplexMethodIdentificationEngine.class.getName());

    //TODO: Find a sensible value for this
    private static final double LIKELIHOOD_THRESHOLD = 0.5;

    @Override
    public Set<ComplexMethodIdentification> process(PsiElement element) {
        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = PsiTreeUtil.collectElementsOfType(element, PsiMethod.class);
        Set<ComplexMethodIdentification> result = new HashSet<ComplexMethodIdentification>();
        for (PsiMethod method : methods) {
            Optional<ComplexMethodIdentification> id = identifyLongMethod(method);
            if (id.isPresent()) {
                result.add(id.get());
            }
        }

        return result;
    }

    private Optional<ComplexMethodIdentification> identifyLongMethod(PsiMethod method) {
        PsiCodeBlock body = method.getBody();
        int loc = LineUtil.countLines(body);
        int cyclomaticComplexity = MetricsUtil.getCyclomaticComplexity(method);
        PsiParameterList parameterList = method.getParameterList();
        int numParameters = parameterList.getParametersCount();
        int nestedBlockDepth = MetricsUtil.getNestedBlockDepth(method);

        LOG.debug("LOC (" + method.getName() + "): " + loc);
        LOG.debug("Cyclomatic complexity (" + method.getName() + "):" + cyclomaticComplexity);
        LOG.debug("Num Parameters (" + method.getName() + "):" + numParameters);
        LOG.debug("Nested Block Depth (" + method.getName() + "):" + nestedBlockDepth);

        double z = -11.336 + 0.598 * cyclomaticComplexity - 0.057 * loc + 4.701 * nestedBlockDepth + 0.486 * numParameters;

        double likelihood = 1 / (1 + Math.exp(-z));
        LOG.debug("LIKELIHOOD (" + method.getName() + "): " + likelihood);

        if (likelihood > LIKELIHOOD_THRESHOLD) {
            return Optional.of(new ComplexMethodIdentification(method));
        } else {
            return Optional.absent();
        }
    }
}
