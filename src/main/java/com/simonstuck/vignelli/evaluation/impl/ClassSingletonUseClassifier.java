package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.StaticCallSingletonEvaluation;
import com.simonstuck.vignelli.inspection.identification.engine.impl.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.DirectSingletonUseIdentification;
import com.simonstuck.vignelli.psi.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassSingletonUseClassifier implements PsiElementEvaluator<Set<StaticCallSingletonEvaluation>> {

    public static final String[] DIALOG_OPTIONS = new String[]{"yes", "no"};

    @Override
    public EvaluationResult<Set<StaticCallSingletonEvaluation>> evaluate(@NotNull PsiElement element) {
        PsiClass clazz = (PsiClass) element;

        Set<StaticCallSingletonEvaluation> evaluations = new HashSet<StaticCallSingletonEvaluation>();

        @SuppressWarnings("unchecked")
        final Collection<PsiMethodCallExpression> methodCallExpressions = PsiTreeUtil.collectElementsOfType(clazz, PsiMethodCallExpression.class);
        final DirectSingletonUseIdentificationEngine directSingletonUseIdentificationEngine = new DirectSingletonUseIdentificationEngine();
        for (PsiMethodCallExpression expression : methodCallExpressions) {
            PsiMethod method = expression.resolveMethod();
            if (method != null && method.hasModifierProperty(PsiModifier.STATIC)) {
                final Set<DirectSingletonUseIdentification> identifications = directSingletonUseIdentificationEngine.process(expression);


                boolean manualClassification = false;

                if (method.getName().equals("getInstance")) {
                    EditorUtil.navigateToElement(expression);

                    PsiClass theClazz = PsiTreeUtil.getParentOfType(method, PsiClass.class);
                    String theClazzName = theClazz != null ? theClazz.getQualifiedName() : "SomeClazz";
                    int selection = Messages.showDialog(theClazzName + "::" + method.getName(), "Is call an instance retrieval call?", DIALOG_OPTIONS, 1, 0, Messages.getQuestionIcon(), null);
                    manualClassification = selection == 0;
                }


                boolean vignelliClassification = !identifications.isEmpty() ? identifications.iterator().next().getMethodCall() == expression : false;
                final StaticCallSingletonEvaluation callEvaluation = new StaticCallSingletonEvaluation(expression.getText(), vignelliClassification, manualClassification);
                evaluations.add(callEvaluation);
            }
        }
        return new PsiElementEvaluator.EvaluationResult.Default<Set<StaticCallSingletonEvaluation>>(EvaluationResult.Outcome.COMPLETED, evaluations);
    }
}
