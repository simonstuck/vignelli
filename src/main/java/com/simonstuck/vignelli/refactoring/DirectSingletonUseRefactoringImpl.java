package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.steps.ConvertToConstructorAssignedFieldRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectSingletonUseRefactoringImpl implements Refactoring {

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/directSingletonUseRefactoring.html";
    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodSingletonStepDescription.html";
    private final Project project;
    private final RefactoringTracker tracker;
    private final PsiMethodCallExpression getInstanceElement;
    private ExtractMethodRefactoringStep extractMethodRefactoringStep;
    private int currentStepIndex = 0;
    private ExtractMethodRefactoringStep.Result extractMethodResult;
    private ConvertToConstructorAssignedFieldRefactoringStep convertToConstructorAssignedFieldRefactoringStep;

    public DirectSingletonUseRefactoringImpl(PsiMethodCallExpression getInstanceElement, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        this.project = project;
        Set<PsiElement> elementsToExtract = new HashSet<PsiElement>();
        this.getInstanceElement = getInstanceElement;
        elementsToExtract.add(this.getInstanceElement);
        extractMethodRefactoringStep = new ExtractMethodRefactoringStep(elementsToExtract, file, project, EXTRACT_METHOD_DESCRIPTION_PATH);
    }

    @Override
    public boolean hasNextStep() {
        return currentStepIndex < 2;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        switch (currentStepIndex) {
            case 0:
                extractMethodResult = extractMethodRefactoringStep.process();
                Collection<PsiExpression> expressions = PsiTreeUtil.collectElementsOfType(extractMethodResult.getExtractedMethod().getBody(), PsiExpression.class);
                PsiExpression expression = expressions.iterator().next();
                convertToConstructorAssignedFieldRefactoringStep = new ConvertToConstructorAssignedFieldRefactoringStep(expression, project);
                break;
            case 1:
                convertToConstructorAssignedFieldRefactoringStep.process();
                break;
        }
        currentStepIndex++;
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
        switch (currentStepIndex) {
            case 0:
                extractMethodRefactoringStep.describeStep(templateValues);
                break;
            case 1:
                convertToConstructorAssignedFieldRefactoringStep.describeStep(templateValues);
                break;
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
        return IOUtils.tryReadFile(DESCRIPTION_TEMPLATE);
    }
}
