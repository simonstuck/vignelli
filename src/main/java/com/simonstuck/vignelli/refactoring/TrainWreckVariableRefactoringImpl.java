package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.refactoring.steps.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrainWreckVariableRefactoringImpl implements Refactoring {

    private static final Logger LOG = Logger.getInstance(TrainWreckVariableRefactoringImpl.class.getName());

    @SuppressWarnings("unchecked")
    private static final Class<? extends RefactoringStep>[] STEPS
            = (Class<? extends RefactoringStep>[]) Arrays.asList(InlineVariableRefactoringStep.class).toArray();
    public static final String TRAIN_WRECK_REFACTORING_DESCRIPTION = "Train Wreck Refactoring";
    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;
    private final RefactoringTracker refactoringTracker;

    private int currentStepIndex = 0;
    private Map<String, Object> refactoringStepArguments;

    public TrainWreckVariableRefactoringImpl(PsiElement trainWreckElement, PsiLocalVariable variable, RefactoringTracker refactoringTracker) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
        this.refactoringTracker = refactoringTracker;

        refactoringStepArguments = new HashMap<>();
        refactoringStepArguments.put(InlineVariableRefactoringStep.PROJECT_ARGUMENT_KEY, variable.getProject());
        refactoringStepArguments.put(InlineVariableRefactoringStep.VARIABLE_TO_INLINE_ARGUMENT_KEY, variable);
    }

    public String description() {
        return TRAIN_WRECK_REFACTORING_DESCRIPTION;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        TrainWreckVariableRefactoringImpl that = (TrainWreckVariableRefactoringImpl) object;

        return trainWreckElement.equals(that.trainWreckElement) && variable.equals(that.variable);
    }

    @Override
    public int hashCode() {
        int result = trainWreckElement.hashCode();
        result = 31 * result + variable.hashCode();
        return result;
    }

    @Override
    public boolean hasNextStep() {
        return currentStepNumber() <= totalSteps();
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        if (currentStepIndex == STEPS.length) {
            throw new NoSuchMethodException("No more refactoring steps required.");
        }

        Class<? extends RefactoringStep> stepClass = STEPS[currentStepIndex];
        try {
            RefactoringStep step = stepClass.getConstructor(Map.class).newInstance(refactoringStepArguments);
            refactoringStepArguments = step.process();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        currentStepIndex++;
    }

    @Override
    public int totalSteps() {
        return STEPS.length;
    }

    @Override
    public int currentStepNumber() {
        return currentStepIndex + 1;
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("totalSteps", totalSteps());
        templateValues.put("currentStep", currentStepNumber());
        templateValues.put("hasNextStep", hasNextStep());

        if (hasNextStep()) {
            templateValues.put("nextStepName", "Inline!");
            templateValues.put("nextStepDescription", "Some inlining Description");
        }
    }

    @Override
    public void begin() {
        refactoringTracker.add(this);
    }

    @Override
    public void complete() {
        refactoringTracker.remove(this);
    }

    @Override
    public String template() {
        try {
            return IOUtils.readFile(getClass().getResource("/descriptionTemplates/trainWreckRefactoring.html").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }
}
