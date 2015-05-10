package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.MethodClassification;
import com.simonstuck.vignelli.psi.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

public class IntelliJMethodClassifier implements PsiElementEvaluator<MethodClassification> {

    public static final String[] DIALOG_OPTIONS = new String[]{"complex", "not complex", "cancel", "stop and save"};

    @Override
    public EvaluationResult<MethodClassification> evaluate(@NotNull PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return null;
        }
        PsiMethod method = (PsiMethod) element;
        EditorUtil.navigateToElement(method);

        int selection = Messages.showDialog(/*clazz.getQualifiedName() + */":" + method.getName(), "Is This Method Complex?", DIALOG_OPTIONS, 1, 0, Messages.getQuestionIcon(), null);

        switch (selection) {
            case 2:
                // cancel
                return new EvaluationResult.Default<MethodClassification>(EvaluationResult.Outcome.CANCELLED_WITHOUT_SAVE, null);
            case 3:
                // cancel and save
                return new EvaluationResult.Default<MethodClassification>(EvaluationResult.Outcome.CANCELLED_WITH_SAVE, null);
            default:
                boolean isComplex = selection == 0;
                MethodClassification classification = new MethodClassification(isComplex);
                return new EvaluationResult.Default<MethodClassification>(EvaluationResult.Outcome.COMPLETED, classification);

        }
    }
}
