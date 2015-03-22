package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TrainWreckVariableRefactoringImpl implements Refactoring {

    public static final String TRAIN_WRECK_REFACTORING_DESCRIPTION = "Train Wreck Refactoring";
    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;
    private final RefactoringTracker refactoringTracker;
    private final Project project;
    private final PsiFile file;

    private int currentStepIndex = 0;
    private Map<String, Object> refactoringStepArguments;
    private Optional<RenameMethodRefactoringStep.Result> renameMethodResult = Optional.empty();

    public TrainWreckVariableRefactoringImpl(PsiElement trainWreckElement, PsiLocalVariable variable, RefactoringTracker refactoringTracker) {
        this.trainWreckElement = trainWreckElement;
        this.variable = variable;
        this.refactoringTracker = refactoringTracker;
        this.project = trainWreckElement.getProject();
        this.file = trainWreckElement.getContainingFile();

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

        switch (currentStepIndex) {
            case 0:
                performInlineStep();
                break;
            case 1:
                performExtractMethodStep();
                break;
            case 2:
                performMoveMethodStep();
                break;
            case 3:
                performRenameMethodStep();
                break;
            default:
                throw new NoSuchMethodException("No more refactoring steps required.");
        }
        currentStepIndex++;
    }

    private void performRenameMethodStep() {
        PsiMethod method = (PsiMethod) refactoringStepArguments.get("targetMethod");
        RenameMethodRefactoringStep step = new RenameMethodRefactoringStep(method,project);
        renameMethodResult = Optional.of(step.process());
    }

    private void performMoveMethodStep() {
        PsiMethod method = (PsiMethod) refactoringStepArguments.get("extractedMethod");
        MoveMethodRefactoringStep step = new MoveMethodRefactoringStep(project, method);
        refactoringStepArguments = step.process();
    }

    private void performInlineStep() {
        InlineVariableRefactoringStep step = new InlineVariableRefactoringStep(refactoringStepArguments);
        refactoringStepArguments = step.process();
    }

    private void performExtractMethodStep() {
        @SuppressWarnings("unchecked")
        Collection<PsiStatement> inlineParents = (Collection<PsiStatement>) refactoringStepArguments.get("inlineParents");
        PsiElement[] elementsToExtract = inlineParents.toArray(new PsiElement[inlineParents.size()]);
        refactoringStepArguments.put("project", project);
        refactoringStepArguments.put("elementsToExtract", elementsToExtract);
        refactoringStepArguments.put("file", file);
        ExtractMethodRefactoringStep step = new ExtractMethodRefactoringStep(refactoringStepArguments);
        refactoringStepArguments = step.process();
    }

    @Override
    public int totalSteps() {
        return 4;
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
