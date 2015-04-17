package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiStatement;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class TrainWreckExpressionRefactoringImpl implements Refactoring {

    private final Collection<PsiStatement> extractRegion;
    private final RefactoringTracker tracker;
    private final Project project;
    private final PsiFile file;
    private int currentStepIndex = 0;

    private ExtractMethodRefactoringStep.Result extractMethodResult;
    private MoveMethodRefactoringStep.Result moveMethodResult;
    private ExtractMethodRefactoringStep extractMethodStep;
    private Refactoring introduceParameterRefactoring;

    public TrainWreckExpressionRefactoringImpl(@NotNull Collection<PsiStatement> extractRegion, RefactoringTracker tracker, Project project, PsiFile file) {
        this.extractRegion = extractRegion;
        this.tracker = tracker;
        this.project = project;
        this.file = file;

        extractMethodStep = new ExtractMethodRefactoringStep(extractRegion,file,project);
    }

    @Override
    public boolean hasNextStep() {
        return currentStepIndex < 4;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        switch (currentStepIndex) {
            case 0:
                extractMethodResult = extractMethodStep.process();
                introduceParameterRefactoring = new IntroduceParametersForMembersRefactoringImpl(extractMethodResult.getExtractedMethod(), tracker, project, file);
                finishParameterIntroductionIfNothingMoreToIntroduce();
                currentStepIndex++;
                break;
            case 1:
                introduceParameterRefactoring.nextStep();
                finishParameterIntroductionIfNothingMoreToIntroduce();
                break;
            case 2:
                performMoveMethodStep();
                currentStepIndex++;
                break;
            case 3:
                performRenameMethodStep();
                currentStepIndex++;
                break;
            default:
                throw new NoSuchMethodException("No more refactoring steps required.");
        }
    }

    private void finishParameterIntroductionIfNothingMoreToIntroduce() {
        if (!introduceParameterRefactoring.hasNextStep()) {
            currentStepIndex++;
        }
    }

    private void performRenameMethodStep() {
        RenameMethodRefactoringStep step = new RenameMethodRefactoringStep(moveMethodResult.getNewMethod(), project);
        step.process();
    }

    private void performMoveMethodStep() {
        MoveMethodRefactoringStep step = new MoveMethodRefactoringStep(project, extractMethodResult.getExtractedMethod());
        moveMethodResult = step.process();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
        switch (currentStepIndex) {
            case 0:
                extractMethodStep.describeStep(templateValues);
                break;
            case 1:
                introduceParameterRefactoring.fillTemplateValues(templateValues);
            default:
        }
    }

    @Override
    public void begin() {
        tracker.add(this);
    }

    @Override
    public void complete() {
        tracker.remove(this);
    }

    @Override
    public String template() {
        try {
            return IOUtils.readFile("descriptionTemplates/trainWreckRefactoring.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainWreckExpressionRefactoringImpl that = (TrainWreckExpressionRefactoringImpl) o;

        return extractRegion.equals(that.extractRegion) && !(file != null ? !file.equals(that.file) : that.file != null) && !(project != null ? !project.equals(that.project) : that.project != null) && !(tracker != null ? !tracker.equals(that.tracker) : that.tracker != null);

    }

    @Override
    public int hashCode() {
        int result = extractRegion.hashCode();
        result = 31 * result + (tracker != null ? tracker.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
}
