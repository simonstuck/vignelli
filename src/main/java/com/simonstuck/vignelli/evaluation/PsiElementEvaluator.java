package com.simonstuck.vignelli.evaluation;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiElementEvaluator<T> {

    /**
     * Evaluates the given element.
     * @param element The element to evaluate
     * @return The result of the given type.
     */
    EvaluationResult<T> evaluate(@NotNull PsiElement element);

    public interface EvaluationResult<T> {
        public enum Outcome {
            COMPLETED, CANCELLED_WITH_SAVE, CANCELLED_WITHOUT_SAVE;
        }

        /**
         * Gets the {@link com.simonstuck.vignelli.evaluation.PsiElementEvaluator.EvaluationResult.Outcome} of the evaluation.
         * @return The outcome of the evaluation.
         */
        @NotNull
        Outcome getOutcome();

        /**
         * Gets the actual evaluation result of the given type
         * @return The actual result of the evaluation if the outcome is COMPLETED.
         */
        @Nullable
        T getEvaluation();

        /**
         * A default implementation of a {@link com.simonstuck.vignelli.evaluation.PsiElementEvaluator.EvaluationResult}.
         * @param <T> The type of the actual evaluation
         */
        public class Default<T> implements EvaluationResult<T> {

            @NotNull
            private final Outcome outcome;
            @Nullable
            private final T evaluation;

            public Default(@NotNull Outcome outcome, @Nullable T evaluation) {
                this.outcome = outcome;
                this.evaluation = evaluation;
            }

            @NotNull
            @Override
            public Outcome getOutcome() {
                return outcome;
            }

            @Nullable
            @Override
            public T getEvaluation() {
                return evaluation;
            }
        }

    }
}
