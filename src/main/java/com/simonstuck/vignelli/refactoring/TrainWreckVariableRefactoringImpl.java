package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.ExtractParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RenameMethodRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrainWreckVariableRefactoringImpl implements Refactoring {

    public static final String TRAIN_WRECK_REFACTORING_DESCRIPTION = "Train Wreck Refactoring";
    private final PsiElement trainWreckElement;
    private final PsiLocalVariable variable;
    private final RefactoringTracker refactoringTracker;
    private final Project project;
    private final PsiFile file;

    private int currentStepIndex = 0;

    private RenameMethodRefactoringStep.Result renameMethodResult;
    private InlineVariableRefactoringStep.Result inlineVariableResult;
    private ExtractMethodRefactoringStep.Result extractMethodResult;
    private MoveMethodRefactoringStep.Result moveMethodResult;
    private Collection<PsiElement> fieldParameters;
    private Iterator<PsiElement> fieldIterator;

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
        return currentStepNumber() <= totalSteps();
    }

    @Override
    public void nextStep() throws NoSuchMethodException {

        switch (currentStepIndex) {
            case 0:
                performInlineStep();
                currentStepIndex++;
                break;
            case 1:
                performExtractMethodStep();
                prepareExtractFieldParameters();
                currentStepIndex++;
                break;
            case 2:
                ExtractParameterRefactoringStep step = new ExtractParameterRefactoringStep(project, file, fieldIterator.next());
                step.process();
                if (!fieldIterator.hasNext()) {
                    currentStepIndex++;
                }
                break;
            case 3:
                performMoveMethodStep();
                currentStepIndex++;
                break;
            case 4:
                performRenameMethodStep();
                currentStepIndex++;
                break;
            default:
                throw new NoSuchMethodException("No more refactoring steps required.");
        }
    }

    private void performRenameMethodStep() {
        RenameMethodRefactoringStep step = new RenameMethodRefactoringStep(moveMethodResult.getNewMethod(), project);
        renameMethodResult = step.process();
    }

    private void performMoveMethodStep() {
        MoveMethodRefactoringStep step = new MoveMethodRefactoringStep(project, extractMethodResult.getExtractedMethod());
        moveMethodResult = step.process();
    }

    private void performInlineStep() {
        InlineVariableRefactoringStep step = new InlineVariableRefactoringStep(variable, project);
        inlineVariableResult = step.process();
    }

    private void performExtractMethodStep() {
        Collection<PsiStatement> inlineParents = inlineVariableResult.getAffectedStatements();
        PsiElement[] elementsToExtract = inlineParents.toArray(new PsiElement[inlineParents.size()]);
        ExtractMethodRefactoringStep step = new ExtractMethodRefactoringStep(elementsToExtract,file,project);
        extractMethodResult = step.process();
    }

    private void prepareExtractFieldParameters() {
        PsiMethod method = extractMethodResult.getExtractedMethod();
        @SuppressWarnings("unchecked") Collection<PsiReferenceExpression> referenceExpressions= PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class);
        fieldParameters = referenceExpressions.stream().filter(new Predicate<PsiReferenceExpression>() {
            @Override
            public boolean test(PsiReferenceExpression psiReferenceExpression) {
                return psiReferenceExpression.resolve() instanceof PsiField;
            }
        }).collect(Collectors.toSet());
        fieldIterator = fieldParameters.iterator();
    }

    @Override
    public int totalSteps() {
        return 5;
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
            fillStepTemplateValues(templateValues);
        }
    }

    private void fillStepTemplateValues(Map<String, Object> templateValues) {
        switch (currentStepIndex) {
            case 0:
                templateValues.put("nextStepName", "Inline!");
                templateValues.put("nextStepDescription", "Some inlining Description");
                break;
            case 1:
                templateValues.put("nextStepName", "Extract Method!");
                templateValues.put("nextStepDescription", "Extract a method now!");
                break;
            case 2:
                templateValues.put("nextStepName", "Move Method!");
                templateValues.put("nextStepDescription", "Now move the method");
                break;
            case 3:
                templateValues.put("nextStepName", "Rename!");
                templateValues.put("nextStepDescription", "Finally, give it a good name!");
                break;
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
}
