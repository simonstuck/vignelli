package com.simonstuck.vignelli.evaluation.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassSingletonUseClassification;
import com.simonstuck.vignelli.evaluation.datamodel.SingletonClassClassification;
import com.simonstuck.vignelli.evaluation.datamodel.SingletonMethodCallPrediction;
import com.simonstuck.vignelli.inspection.identification.engine.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.DirectSingletonUseIdentification;
import com.simonstuck.vignelli.psi.util.ClassUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class ClassSingletonUseClassifier implements PsiElementEvaluator<ClassSingletonUseClassification> {

    @NotNull
    private final Project project;
    @NotNull
    private final PsiElementEvaluator<SingletonClassClassification> classIsSingletonClassifier;

    public ClassSingletonUseClassifier(@NotNull Project project, @NotNull PsiElementEvaluator<SingletonClassClassification> classIsSingletonClassifier) {
        this.project = project;
        this.classIsSingletonClassifier = classIsSingletonClassifier;
    }


    @Override
    public EvaluationResult<ClassSingletonUseClassification> evaluate(@NotNull PsiElement element) {
        PsiClass clazz = (PsiClass) element;

        Collection<PsiMember> allStaticMembers = ClassUtil.getAllNonPrivateStaticMembers(clazz);
        if (allStaticMembers.isEmpty()) {
            // definitely not a singleton, move on
            ClassSingletonUseClassification classification = new ClassSingletonUseClassification(clazz.getQualifiedName(), new SingletonClassClassification(false));
            return new PsiElementEvaluator.EvaluationResult.Default<ClassSingletonUseClassification>(PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED, classification);
        } else {
            // ask if singleton
            PsiElementEvaluator.EvaluationResult<SingletonClassClassification> classificationResult = classIsSingletonClassifier.evaluate(clazz);

            if (classificationResult.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                assert  classificationResult.getEvaluation() != null;
                ClassSingletonUseClassification classification = new ClassSingletonUseClassification(clazz.getQualifiedName(), classificationResult.getEvaluation());
                // go through all calls to all static methods and check

                for (PsiMember method : allStaticMembers) {
                    Collection<PsiReference> methodCalls = ReferencesSearch.search(method, GlobalSearchScope.allScope(project)).findAll();
                    for (PsiReference staticRef : methodCalls) {
                        classification.addMethodCallPrediction(staticRef.getElement().getText(), new SingletonMethodCallPrediction(isReferenceIdentifiedAsDirectSingletonUse(staticRef)));
                    }
                }
                return new PsiElementEvaluator.EvaluationResult.Default<ClassSingletonUseClassification>(classificationResult.getOutcome(), classification);
            } else {
                return new PsiElementEvaluator.EvaluationResult.Default<ClassSingletonUseClassification>(classificationResult.getOutcome(), null);
            }
        }
    }

    private boolean isReferenceIdentifiedAsDirectSingletonUse(PsiReference staticRef) {
        PsiClass parentClazz = PsiTreeUtil.getParentOfType(staticRef.getElement(), PsiClass.class, false);
        if (parentClazz != null) {
            Set<DirectSingletonUseIdentification> identifications = new DirectSingletonUseIdentificationEngine().process(parentClazz);

            for (DirectSingletonUseIdentification id : identifications) {
                if (PsiTreeUtil.isAncestor(id.getMethodCall(), staticRef.getElement(), false)) {
                    return true;
                }
            }
        }
        return false;
    }
}
