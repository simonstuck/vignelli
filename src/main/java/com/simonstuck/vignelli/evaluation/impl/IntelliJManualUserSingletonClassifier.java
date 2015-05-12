package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.SingletonClassClassification;
import com.simonstuck.vignelli.evaluation.ui.ListSelectionDialog;
import com.simonstuck.vignelli.psi.util.ClassUtil;
import com.simonstuck.vignelli.psi.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IntelliJManualUserSingletonClassifier implements PsiElementEvaluator<SingletonClassClassification> {

    public static final String[] DIALOG_OPTIONS = new String[]{"Singleton", "No Singleton", "cancel", "stop and save"};

    @Override
    public EvaluationResult<SingletonClassClassification> evaluate(@NotNull PsiElement element) {
        if (!(element instanceof PsiClass)) {
            return null;
        }
        PsiClass clazz = (PsiClass) element;
        EditorUtil.navigateToElement(clazz);

        String[] candidates = getInstanceRetrievalCandidates(clazz);
        ListSelectionDialog listSelectionDialog = new ListSelectionDialog(element.getProject(), candidates);
        listSelectionDialog.show();
        String[] selectedMembers = listSelectionDialog.getSelectedValues();

        //int selection = Messages.showDialog(/*clazz.getQualifiedName() + */":" + clazz.getName(), "Is This Class a Singleton?", DIALOG_OPTIONS, 1, 0, Messages.getQuestionIcon(), null);

        if (selectedMembers.length == 0) {
            return new EvaluationResult.Default<SingletonClassClassification>(EvaluationResult.Outcome.COMPLETED, new SingletonClassClassification(false));
        } else {
            SingletonClassClassification classification = new SingletonClassClassification(true);
            for (String selectedMember : selectedMembers) {
                classification.addInstanceRetrievalMember(selectedMember);
            }
            return new EvaluationResult.Default<SingletonClassClassification>(EvaluationResult.Outcome.COMPLETED, classification);
        }
    }

    private String[] getInstanceRetrievalCandidates(PsiClass clazz) {
        Collection<PsiMember> allNonPrivateStaticMethods = ClassUtil.getAllNonPrivateStaticMethods(clazz);
        List<String> instanceRetrievalCandidateMembers = new ArrayList<String>(allNonPrivateStaticMethods.size());
        for (PsiMember member : allNonPrivateStaticMethods) {
            instanceRetrievalCandidateMembers.add(member.getName());
        }
        return instanceRetrievalCandidateMembers.toArray(new String[instanceRetrievalCandidateMembers.size()]);
    }
}
