package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.utils.IOUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectSingletonUseRefactoringImpl implements Refactoring {

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/directSingletonUseRefactoring.html";
    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodSingletonStepDescription.html";
    private final RefactoringTracker tracker;
    private ExtractMethodRefactoringStep extractMethodRefactoringStep;

    public DirectSingletonUseRefactoringImpl(PsiMethodCallExpression getInstanceElement, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        Set<PsiElement> elementsToExtract = new HashSet<PsiElement>();
        elementsToExtract.add(getInstanceElement);
        extractMethodRefactoringStep = new ExtractMethodRefactoringStep(elementsToExtract, file, project, EXTRACT_METHOD_DESCRIPTION_PATH);
    }

    @Override
    public boolean hasNextStep() {
        return extractMethodRefactoringStep != null;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        extractMethodRefactoringStep.process();
        extractMethodRefactoringStep = null;
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put("hasNextStep", hasNextStep());
        extractMethodRefactoringStep.describeStep(templateValues);
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
