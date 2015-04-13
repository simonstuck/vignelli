package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.ExtractParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrainWreckExpressionRefactoringImpl implements Refactoring {

    private final Collection<PsiStatement> extractRegion;
    private final RefactoringTracker tracker;
    private final Project project;
    private final PsiFile file;
    private int currentStepIndex = 0;

    private ExtractMethodRefactoringStep.Result extractMethodResult;
    private MoveMethodRefactoringStep.Result moveMethodResult;
    private Iterator<PsiElement> fieldIterator;

    public TrainWreckExpressionRefactoringImpl(@NotNull Collection<PsiStatement> extractRegion, RefactoringTracker tracker, Project project, PsiFile file) {
        this.extractRegion = extractRegion;
        this.tracker = tracker;
        this.project = project;
        this.file = file;
    }

    @Override
    public boolean hasNextStep() {
        return currentStepIndex < 4;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        switch (currentStepIndex) {
            case 0:
                performExtractMethodStep();
                prepareExtractFieldParameters();
                currentStepIndex++;
                if (!fieldIterator.hasNext()) {
                    currentStepIndex++;
                }
                break;
            case 1:
                ExtractParameterRefactoringStep step = new ExtractParameterRefactoringStep(project, file, fieldIterator.next());
                step.process();
                if (!fieldIterator.hasNext()) {
                    currentStepIndex++;
                }
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


    private void performRenameMethodStep() {
        RenameMethodRefactoringStep step = new RenameMethodRefactoringStep(moveMethodResult.getNewMethod(), project);
        step.process();
    }

    private void performMoveMethodStep() {
        MoveMethodRefactoringStep step = new MoveMethodRefactoringStep(project, extractMethodResult.getExtractedMethod());
        moveMethodResult = step.process();
    }

    private void performExtractMethodStep() {
        PsiElement[] elementsToExtract;
        elementsToExtract = extractRegion.toArray(new PsiElement[extractRegion.size()]);
        ExtractMethodRefactoringStep step = new ExtractMethodRefactoringStep(elementsToExtract,file,project);
        extractMethodResult = step.process();
    }

    private void prepareExtractFieldParameters() {
        PsiMethod method = extractMethodResult.getExtractedMethod();
        @SuppressWarnings("unchecked") Collection<PsiReferenceExpression> referenceExpressions= PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);
        Collection<PsiElement> fieldParameters = referenceExpressions.stream().filter(new Predicate<PsiReferenceExpression>() {
            @Override
            public boolean test(PsiReferenceExpression psiReferenceExpression) {
                PsiElement resolved = psiReferenceExpression.resolve();
                return resolved instanceof PsiField || (resolved instanceof PsiMethod && ((PsiMethod) resolved).getContainingClass().equals(extractMethodResult.getExtractedMethod().getContainingClass()));
            }
        }).collect(Collectors.toSet());
        fieldIterator = fieldParameters.iterator();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
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
            return IOUtils.readFile(getClass().getResource("/descriptionTemplates/trainWreckRefactoring.html").toURI());
        } catch (IOException | URISyntaxException e) {
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
