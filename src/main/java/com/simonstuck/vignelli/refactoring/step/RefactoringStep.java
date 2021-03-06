package com.simonstuck.vignelli.refactoring.step;

import java.util.Map;

/**
 * This represents a refactoring step in a multi-step refactoring process.
 *
 * Refactoring step are able to express the goal of a refactoring step internally and
 * can start listening for this goal. Once a step is listening for its goal, any
 * changes that are made to the PsiTree are checked by the refactoring step as to whether
 * they have lead to the goal. If so, the {@link com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate}
 * is notified with the result of the refactoring if a delegate is given.
 */
public interface RefactoringStep {

    /**
     * The key used for the name of this refactoring step in the template engine.
     */
    public static final String STEP_NAME_TEMPLATE_KEY = "nextStepName";

    /**
     * The key used for the description of this refactoring step in the template engine.
     */
    public static final String STEP_DESCRIPTION_TEMPLATE_KEY = "nextStepDescription";


    /**
     * Start observing the PsiTree for the goal that is expressed in the refactoring step.
     */
    void start();

    /**
     * Stop observing PsiTree changes.
     */
    void end();

    /**
     * Manually trigger the required refactoring procedures in IntelliJ.
     * @return The result of the procedure.
     */
    void process();

    /**
     * Describe the step by adding descriptive key-value pairs to the template store.
     * @param templateValues The template store that will be used to render the UI for the step.
     */
    void describeStep(Map<String, Object> templateValues);


    /**
     * Accepts the given visitor and sends the appropriate method on it.
     * @param refactoringStepVisitor The visitor to use.
     */
    void accept(RefactoringStepVisitor refactoringStepVisitor);
}
