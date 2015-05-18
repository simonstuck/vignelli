package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.simonstuck.vignelli.inspection.identification.engine.impl.TrainWreckIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.TrainWreckIdentification;
import com.simonstuck.vignelli.psi.impl.IntelliJClassFinderAdapter;
import com.simonstuck.vignelli.psi.util.MethodCallUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.MoveMethodRefactoringStep;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Creates the correct move method refactoring step depending on whether a critical chain was extracted or not.
 */
class TrainWreckMoveMethodRefactoringStepCreation {
    private final PsiMethod methodToMove;
    private final Project project;
    private final IntroduceParametersForCriticalCallsImpl.Result introduceParameterForCriticalChainResult;
    private final RefactoringStepDelegate delegate;
    private final IntelliJClassFinderAdapter classFinder;

    public TrainWreckMoveMethodRefactoringStepCreation(
            @NotNull PsiMethod methodToMove,
            @NotNull Project project,
            @Nullable IntroduceParametersForCriticalCallsImpl.Result introduceParameterForCriticalChainResult,
            @NotNull RefactoringStepDelegate delegate,
            @NotNull IntelliJClassFinderAdapter classFinder
    ) {
        this.methodToMove = methodToMove;
        this.project = project;
        this.introduceParameterForCriticalChainResult = introduceParameterForCriticalChainResult;
        this.delegate = delegate;
        this.classFinder = classFinder;
    }

    public MoveMethodRefactoringStep invoke() {
        PsiExpression targetExpression = getTargetExpression();
        if (targetExpression == null) {
            return null;
        } else {
            return new MoveMethodRefactoringStep(project, methodToMove, targetExpression, ApplicationManager.getApplication(), delegate);
        }
    }

    /**
     * Checks whether the critical method call chain was extracted using the result which may be null.
     * @return True iff the critical method call chain was indeed extracted, false otherwise.
     */
    @Contract
    private boolean hasExtractedCriticalChain() {
        return introduceParameterForCriticalChainResult != null && !introduceParameterForCriticalChainResult.getResults().isEmpty();
    }


    /**
     * Computes the target expression for the move operation, i.e. the expression where the method should be moved.
     * @return The target expression if it can be computed, null otherwise.
     */
    private PsiExpression getTargetExpression() {
        if (hasExtractedCriticalChain()) {
            assert introduceParameterForCriticalChainResult != null;
            final IntroduceParameterRefactoringStep.Result firstNewParameter = introduceParameterForCriticalChainResult.getResults().iterator().next();
            return findFirstReferenceExprToParameter(firstNewParameter.getNewParameter(), new LocalSearchScope(methodToMove));
        } else {
            return getTargetExpressionFromFirstIdentifiableTrainWreck();
        }
    }

    /**
     * Finds the first reference to the given parameter in the given scope.
     * @param parameter The parameter for which to find the first reference
     * @param searchScope The scope to search.
     * @return The first reference to the given parameter if it can be found, null otherwise.
     */
    private PsiExpression findFirstReferenceExprToParameter(PsiParameter parameter, SearchScope searchScope) {
        PsiExpression refExpr = null;
        final Collection<PsiReference> allRefs = ReferencesSearch.search(parameter, searchScope).findAll();
        if (allRefs.isEmpty()) {
            return null;
        } else {
            final Iterator<PsiReference> referenceIterator = allRefs.iterator();
            while (referenceIterator.hasNext() && refExpr == null) {
                final PsiReference nextRef = referenceIterator.next();
                if (nextRef instanceof PsiExpression) {
                    refExpr = (PsiExpression) nextRef;
                }
            }
            return refExpr;
        }
    }

    /**
     * Finds the target expression using the first identifiable train wreck in the given code.
     * @return The expression of where to move the method to if it exists, null otherwise.
     */
    @Nullable
    private PsiExpression getTargetExpressionFromFirstIdentifiableTrainWreck() {
        TrainWreckIdentificationEngine engine = new TrainWreckIdentificationEngine(classFinder);
        Set<TrainWreckIdentification> methodChains = engine.process(methodToMove);
        if (!methodChains.isEmpty()) {
            TrainWreckIdentification first = methodChains.iterator().next();
            return MethodCallUtil.getFinalQualifier(first.getFinalCall());
        } else {
            return null;
        }
    }

}
