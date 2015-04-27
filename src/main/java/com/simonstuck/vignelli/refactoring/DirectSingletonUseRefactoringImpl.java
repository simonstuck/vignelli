package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
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

        currentRefactoringStep = new ExtractMethodRefactoringStep(Collections.singleton(getInstanceElement), file, project, EXTRACT_METHOD_DESCRIPTION_PATH,PsiManager.getInstance(project),this);
        currentRefactoringStep.startListeningForGoal();
    }

    @Override
    public boolean hasNextStep() {
        return currentRefactoringStep != null;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {
        currentRefactoringStep.process();
    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {
        templateValues.put(HAS_NEXT_STEP_TEMPLATE_KEY, hasNextStep());
        currentRefactoringStep.describeStep(templateValues);
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
        currentRefactoringStep.endListeningForGoal();

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
            currentRefactoringStep.startListeningForGoal();
        }
        setChanged();
        notifyObservers();
    }

    private ConvertToConstructorAssignedFieldRefactoringStep createCovertToConstructorAssignedFieldRefactoringStep() {
        PsiMethod extractedMethod = extractMethodResult.getExtractedMethod();
        @SuppressWarnings("unchecked")
        Collection<PsiExpression> expressions = PsiTreeUtil.collectElementsOfType(extractedMethod.getBody(), PsiExpression.class);
        PsiExpression expression = expressions.iterator().next();
        return new ConvertToConstructorAssignedFieldRefactoringStep(expression, project, PsiManager.getInstance(project), this);
    }

    private ExtractInterfaceRefactoringStep createExtractInterfaceRefactoringStep() {
        return new ExtractInterfaceRefactoringStep(project, singletonClass, currentClass);
    }

    private IntroduceParameterRefactoringStep createIntroduceParameterRefactoringStep() {
        return new IntroduceParameterRefactoringStep(
                project,
                file,
                convertToConstructorAssignedFieldStepResult.getConstructorExpression(),
                INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH,
                PsiManager.getInstance(project),
                this
        );
    }

    private InlineMethodRefactoringStep createInlineMethodRefactoringStep() {
        return new InlineMethodRefactoringStep(project, extractMethodResult.getExtractedMethod(), PsiManager.getInstance(project), this);
    }
}
