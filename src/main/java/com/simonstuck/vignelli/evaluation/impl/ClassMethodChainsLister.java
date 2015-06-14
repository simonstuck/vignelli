package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.MethodChainData;
import com.simonstuck.vignelli.inspection.identification.engine.impl.TrainWreckIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.MethodChain;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClassMethodChainsLister implements PsiElementEvaluator<List<MethodChainData>> {

    public static final String[] DIALOG_OPTIONS = new String[]{"yes", "no"};

    @Override
    public EvaluationResult<List<MethodChainData>> evaluate(@NotNull PsiElement element) {
        PsiClass clazz = (PsiClass) element;


        @SuppressWarnings("unchecked")
        final Collection<PsiMethodCallExpression> methodCallExpressions = PsiTreeUtil.collectElementsOfType(clazz, PsiMethodCallExpression.class);

        Set<MethodChain> chains = new HashSet<MethodChain>();
        for (PsiMethodCallExpression call : methodCallExpressions) {
            final MethodChain methodChain = new MethodChain(call, new IntelliJClassFinderAdapter(element.getProject()));
            if (methodChain.getLength() >= 3) {
                chains.add(methodChain);
            }
        }

        chains.removeAll(allMethodChainQualifiers(chains));

        List<MethodChainData> results = new LinkedList<MethodChainData>();


        TrainWreckIdentificationEngine engine = new TrainWreckIdentificationEngine(new IntelliJClassFinderAdapter(element.getProject()));
        for (MethodChain chain : chains) {
            PsiMethod method = PsiTreeUtil.getParentOfType(chain.getFinalCall(), PsiMethod.class);
            String methodName = method != null ? method.getName() : "";
            final Set<TrainWreckIdentification> trainWrecks = engine.process(chain.getFinalCall());
            boolean isTrainWreck = !trainWrecks.isEmpty() ? trainWrecks.iterator().next().getFinalCall() == chain.getFinalCall() : false;
            results.add(new MethodChainData(clazz.getQualifiedName(), methodName, chain.getFinalCall().getText(), isTrainWreck));
        }
        return new EvaluationResult.Default<List<MethodChainData>>(EvaluationResult.Outcome.COMPLETED, results);
    }



    private Collection<MethodChain> allMethodChainQualifiers(Collection<MethodChain> ids) {
        Collection<MethodChain> result = new LinkedList<MethodChain>();
        for (MethodChain id : ids) {
            result.addAll(id.getAllMethodCallQualifiers());
        }
        return result;
    }
}
