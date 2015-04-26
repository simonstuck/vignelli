package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.steps.ConvertToConstructorAssignedFieldRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.ExtractInterfaceRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.InlineMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStep;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.steps.RefactoringStepResult;
import com.simonstuck.vignelli.utils.IOUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DirectSingletonUseRefactoringImpl implements Refactoring, RefactoringStepDelegate {

    private static final Logger LOG = Logger.getInstance(DirectSingletonUseRefactoringImpl.class.getName());

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/directSingletonUseRefactoring.html";
    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodSingletonStepDescription.html";
    private static final String INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceConstructorParameterStepDescription.html";

    private final Project project;
    private final RefactoringTracker tracker;
    private final PsiClass singletonClass;
    private ExtractMethodRefactoringStep extractMethodRefactoringStep;
    private int currentStepIndex = 0;
    private ExtractMethodRefactoringStep.Result extractMethodResult;
    private ConvertToConstructorAssignedFieldRefactoringStep convertToConstructorAssignedFieldRefactoringStep;
    private InlineMethodRefactoringStep inlineMethodRefactoringStep;
    private ConvertToConstructorAssignedFieldRefactoringStep.Result convertToConstructorAssignedFieldStepResult;
    private final PsiFile file;
    private IntroduceParameterRefactoringStep introduceParameterRefactoringStep;
    private IntroduceParameterRefactoringStep.Result introduceParameterRefactoringStepResult;
    private final ExtractInterfaceRefactoringStep extractInterfaceRefactoringStep;

    public DirectSingletonUseRefactoringImpl(PsiMethodCallExpression getInstanceElement, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        this.project = project;
        extractMethodRefactoringStep = new ExtractMethodRefactoringStep(Collections.singleton(getInstanceElement), file, project, EXTRACT_METHOD_DESCRIPTION_PATH);
        this.file = file;
        this.singletonClass = PsiTreeUtil.getParentOfType(getInstanceElement.getMethodExpression().resolve(), PsiClass.class);
        assert singletonClass != null;
        PsiClass currentClass = PsiTreeUtil.getParentOfType(getInstanceElement, PsiClass.class);
        assert currentClass != null;
        extractInterfaceRefactoringStep = new ExtractInterfaceRefactoringStep(project, singletonClass, currentClass);
    }

    @Override
    public boolean hasNextStep() {
        return currentStepIndex < 5;
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
                convertToConstructorAssignedFieldStepResult = convertToConstructorAssignedFieldRefactoringStep.process();
                inlineMethodRefactoringStep = new InlineMethodRefactoringStep(project, extractMethodResult.getExtractedMethod(), PsiManager.getInstance(project), this);
                inlineMethodRefactoringStep.startListeningForGoal();
                break;
            case 2:
                inlineMethodRefactoringStep.process();
                inlineMethodRefactoringStep.endListeningForGoal();
                introduceParameterRefactoringStep = new IntroduceParameterRefactoringStep(project, file, convertToConstructorAssignedFieldStepResult.getConstructorExpression(), INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH);
                break;
            case 3:
                introduceParameterRefactoringStepResult = introduceParameterRefactoringStep.process();
                break;
            case 4:
                extractInterfaceRefactoringStep.process();
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
            case 2:
                inlineMethodRefactoringStep.describeStep(templateValues);
                break;
            case 3:
                introduceParameterRefactoringStep.describeStep(templateValues);
                break;
            case 4:
                extractInterfaceRefactoringStep.describeStep(templateValues);
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


    @Override
    public void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        LOG.info("didFinishRefactoringStep!");
    }
}
