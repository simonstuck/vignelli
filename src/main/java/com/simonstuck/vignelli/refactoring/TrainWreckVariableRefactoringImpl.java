package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.refactoring.steps.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStep;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrainWreckVariableRefactoringImpl implements Refactoring {

    private static final Logger LOG = Logger.getInstance(TrainWreckVariableRefactoringImpl.class.getName());

    @SuppressWarnings("unchecked")
    private static final Class<? extends RefactoringStep>[] STEPS
            = (Class<? extends RefactoringStep>[]) Arrays.asList(InlineVariableRefactoringStep.class).toArray();
    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;

    private int currentStepIndex = 0;

    public TrainWreckVariableRefactoringImpl(PsiElement trainWreckElement, PsiLocalVariable variable) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
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
    public void nextStep() throws NoSuchMethodException {
        if (currentStepIndex == STEPS.length) {
            throw new NoSuchMethodException("No more refactoring steps required.");
        }

        Class<? extends RefactoringStep> stepClass = STEPS[currentStepIndex];
        try {
            RefactoringStep step = stepClass.getConstructor(Map.class).newInstance(new HashMap<>());
            step.process();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        currentStepIndex++;
    }
}
