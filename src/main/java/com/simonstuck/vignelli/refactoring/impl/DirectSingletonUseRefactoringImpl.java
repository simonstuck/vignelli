package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.impl.ConvertToConstructorAssignedFieldRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractInterfaceRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.InlineMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.util.IOUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DirectSingletonUseRefactoringImpl extends Refactoring implements RefactoringStepDelegate {

    private static final Logger LOG = Logger.getInstance(DirectSingletonUseRefactoringImpl.class.getName());

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/directSingletonUseRefactoring.html";
    private static final String EXTRACT_METHOD_DESCRIPTION_PATH = "descriptionTemplates/extractMethodSingletonStepDescription.html";
    private static final String INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceConstructorParameterStepDescription.html";

    private final Project project;
    private final PsiFile file;
    private final RefactoringTracker tracker;
    private final PsiClass singletonClass;
    private final PsiClass currentClass;

    private ExtractMethodRefactoringStep.Result extractMethodResult;
    private ConvertToConstructorAssignedFieldRefactoringStep.Result convertToConstructorAssignedFieldStepResult;

    private RefactoringStep currentRefactoringStep;

    public DirectSingletonUseRefactoringImpl(PsiMethodCallExpression getInstanceElement, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        this.project = project;

        this.file = file;
        PsiReferenceExpression methodExpression = getInstanceElement.getMethodExpression();
        this.singletonClass = PsiTreeUtil.getParentOfType(methodExpression.resolve(), PsiClass.class);
        assert singletonClass != null;
        currentClass = PsiTreeUtil.getParentOfType(getInstanceElement, PsiClass.class);
        assert currentClass != null;

        currentRefactoringStep = new ExtractMethodRefactoringStep(Collections.singleton(getInstanceElement), file, project, EXTRACT_METHOD_DESCRIPTION_PATH,ApplicationManager.getApplication(),this);
        currentRefactoringStep.start();
    }

    @Override
    public boolean hasNextStep() {
        return currentRefactoringStep != null;
    }

    @Override
    public void nextStep() {
        currentRefactoringStep.process();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put(HAS_NEXT_STEP_TEMPLATE_KEY, hasNextStep());
        if (currentRefactoringStep != null) {
            currentRefactoringStep.describeStep(templateValues);
        }
    }

    @Override
    public void begin() {
        tracker.add(this);
    }

    @Override
    public synchronized void complete() {
        tracker.remove(this);
        if (currentRefactoringStep != null) {
            currentRefactoringStep.end();
        }
    }

    @Override
    public String template() {
        return IOUtil.tryReadFile(DESCRIPTION_TEMPLATE);
    }


    @Override
    public synchronized void didFinishRefactoringStep(RefactoringStep step, RefactoringStepResult result) {
        LOG.info("didFinishRefactoringStep!");
        currentRefactoringStep.end();

        if (!result.isSuccess()) {
            complete();
        }

        if (step instanceof ExtractMethodRefactoringStep) {
            extractMethodResult = (ExtractMethodRefactoringStep.Result) result;
            currentRefactoringStep = createCovertToConstructorAssignedFieldRefactoringStep();
        } else if (step instanceof ConvertToConstructorAssignedFieldRefactoringStep) {
            convertToConstructorAssignedFieldStepResult = (ConvertToConstructorAssignedFieldRefactoringStep.Result) result;
            currentRefactoringStep = createInlineMethodRefactoringStep();
        } else if (step instanceof InlineMethodRefactoringStep) {
            currentRefactoringStep = createIntroduceParameterRefactoringStep();
        } else if (step instanceof IntroduceParameterRefactoringStep) {
            currentRefactoringStep = createExtractInterfaceRefactoringStep();
        } else if (step instanceof ExtractInterfaceRefactoringStep) {
            currentRefactoringStep = null;
        }

        if (currentRefactoringStep != null) {
            currentRefactoringStep.start();
        }
        setChanged();
        notifyObservers();
    }

    private ConvertToConstructorAssignedFieldRefactoringStep createCovertToConstructorAssignedFieldRefactoringStep() {
        PsiMethod extractedMethod = extractMethodResult.getExtractedMethod();
        @SuppressWarnings("unchecked")
        Collection<PsiExpression> expressions = PsiTreeUtil.collectElementsOfType(extractedMethod.getBody(), PsiExpression.class);
        PsiExpression expression = expressions.iterator().next();
        return new ConvertToConstructorAssignedFieldRefactoringStep(expression, project, ApplicationManager.getApplication(), this);
    }

    private ExtractInterfaceRefactoringStep createExtractInterfaceRefactoringStep() {
        return new ExtractInterfaceRefactoringStep(project, ApplicationManager.getApplication(), singletonClass, currentClass, this);
    }

    private IntroduceParameterRefactoringStep createIntroduceParameterRefactoringStep() {
        return new IntroduceParameterRefactoringStep(
                project,
                file,
                convertToConstructorAssignedFieldStepResult.getConstructorExpression(),
                INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH,
                ApplicationManager.getApplication(),
                this
        );
    }

    private InlineMethodRefactoringStep createInlineMethodRefactoringStep() {
        return new InlineMethodRefactoringStep(project, extractMethodResult.getExtractedMethod(), ApplicationManager.getApplication(), this);
    }
}
