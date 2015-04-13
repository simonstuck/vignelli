package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.refactoring.steps.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

public class TrainWreckVariableRefactoringImpl implements Refactoring {

    public static final String TRAIN_WRECK_REFACTORING_DESCRIPTION = "Train Wreck Refactoring";
    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;
    private final RefactoringTracker refactoringTracker;
    private final Project project;
    private final PsiFile file;

    private InlineVariableRefactoringStep.Result inlineVariableResult;
    private TrainWreckExpressionRefactoringImpl trainWreckExprRefactoring;

    public TrainWreckVariableRefactoringImpl(PsiElement trainWreckElement, PsiLocalVariable variable, RefactoringTracker refactoringTracker) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
        this.refactoringTracker = refactoringTracker;
        this.project = trainWreckElement.getProject();
        this.file = trainWreckElement.getContainingFile();
    }

    public String description() {
        return TRAIN_WRECK_REFACTORING_DESCRIPTION;
    }

    @Override
    public boolean hasNextStep() {
        return !hasCompletedVariableInlining() || trainWreckExprRefactoring.hasNextStep();
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        if (!hasCompletedVariableInlining()) {
            performInlineStep();
            launchExpressionRefactoring();
        } else {
            trainWreckExprRefactoring.nextStep();
        }
    }

    private void launchExpressionRefactoring() {
        Collection<PsiStatement> extractRegion = inlineVariableResult.getAffectedStatements();
        trainWreckExprRefactoring = new TrainWreckExpressionRefactoringImpl(extractRegion,refactoringTracker, project, file);
    }

    private void performInlineStep() {
        InlineVariableRefactoringStep step = new InlineVariableRefactoringStep(variable, project);
        inlineVariableResult = step.process();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
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

    private boolean hasCompletedVariableInlining() {
        return inlineVariableResult != null;
    }
}
