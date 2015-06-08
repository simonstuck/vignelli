package com.simonstuck.vignelli.refactoring.impl;

import com.intellij.openapi.application.ApplicationAdapter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.simonstuck.vignelli.psi.ApplicableInterfacesForUsedMethodsInClassSearch;
import com.simonstuck.vignelli.psi.util.PsiElementUtil;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringTracker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepVisitorAdapter;
import com.simonstuck.vignelli.refactoring.step.impl.ConvertToConstructorAssignedFieldRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractInterfaceRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.TypeMigrationRefactoringStep;
import com.simonstuck.vignelli.util.IOUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectSingletonUseRefactoringImpl extends Refactoring implements RefactoringStepDelegate {

    private static final Logger LOG = Logger.getInstance(DirectSingletonUseRefactoringImpl.class.getName());

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/directSingletonUseRefactoring.html";
    private static final String INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH = "descriptionTemplates/introduceConstructorParameterStepDescription.html";
    private static final String SEARCHING_FOR_INTERFACES_MSG = "Searching for existing applicable interfaces";

    private final Project project;
    private final PsiFile file;
    private final RefactoringTracker tracker;
    private final PsiClass singletonClass;
    private final PsiClass currentClass;

    private RefactoringStep currentRefactoringStep;
    private TypeMigrationRefactoringStep typeMigrationRefactoringStep;
    private TypeMigrationParameterUpdater typeMigrationParameterUpdater;
    private Set<PsiClass> allApplicableInterfaces;

    public DirectSingletonUseRefactoringImpl(PsiMethodCallExpression getInstanceElement, RefactoringTracker tracker, Project project, PsiFile file) {
        this.tracker = tracker;
        this.project = project;

        this.file = file;
        PsiReferenceExpression methodExpression = getInstanceElement.getMethodExpression();
        this.singletonClass = PsiTreeUtil.getParentOfType(methodExpression.resolve(), PsiClass.class);
        assert singletonClass != null;
        currentClass = PsiTreeUtil.getParentOfType(getInstanceElement, PsiClass.class);
        assert currentClass != null;

        currentRefactoringStep = initFirstStep(getInstanceElement, project, file);
        currentRefactoringStep.start();
    }

    /**
     * Initialises the first refactoring step in the refactoring process depending on the nature of the problem.
     * @param getInstanceElement The instance retrieval method call.
     * @param project The project
     * @param file The file in which the problem occurs
     * @return The first refactoring step.
     */
    private RefactoringStep initFirstStep(PsiMethodCallExpression getInstanceElement, Project project, PsiFile file) {
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(getInstanceElement, PsiMethod.class);
        LOG.assertTrue(containingMethod != null);
        if (containingMethod.isConstructor()) {
            // we're in a constructor and can simply inject the expression as parameter
            return new IntroduceParameterRefactoringStep(project, file, getInstanceElement, INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
        } else {
            return new ConvertToConstructorAssignedFieldRefactoringStep(getInstanceElement, project, ApplicationManager.getApplication(), this);
        }
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

        if (typeMigrationParameterUpdater != null) {
            ApplicationManager.getApplication().removeApplicationListener(typeMigrationParameterUpdater);
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

        final RefactoringStepCompletionHandler refactoringStepCompletionHandler = new RefactoringStepCompletionHandler(result);
        step.accept(refactoringStepCompletionHandler);

        if (!refactoringStepCompletionHandler.hasBeenHandledSuccessfully()) {
            complete();
            return;
        }
        if (currentRefactoringStep != null) {
            currentRefactoringStep.start();
        }
        setChanged();
        notifyObservers();
    }

    private ExtractInterfaceRefactoringStep createExtractInterfaceRefactoringStep() {
        return new ExtractInterfaceRefactoringStep(project, ApplicationManager.getApplication(), singletonClass, currentClass, this);
    }

    private IntroduceParameterRefactoringStep createIntroduceParameterRefactoringStep(PsiExpression parameterExpression) {
        return new IntroduceParameterRefactoringStep(project, file, parameterExpression, INTRODUCE_CONSTRUCTOR_PARAMETER_STEP_DESCRIPTION_PATH, ApplicationManager.getApplication(), this);
    }

    private class RefactoringStepCompletionHandler extends RefactoringStepVisitorAdapter {

        private final RefactoringStepResult result;
        private boolean success = false;
        private ConvertToConstructorAssignedFieldRefactoringStep.Result convertToConstructorAssignedFieldStepResult;

        private RefactoringStepCompletionHandler(RefactoringStepResult result) {
            this.result = result;
        }

        @Override
        public void visitElement(ConvertToConstructorAssignedFieldRefactoringStep convertToConstructorAssignedFieldRefactoringStep) {
            super.visitElement(convertToConstructorAssignedFieldRefactoringStep);
            convertToConstructorAssignedFieldStepResult = (ConvertToConstructorAssignedFieldRefactoringStep.Result) result;
            currentRefactoringStep = createIntroduceParameterRefactoringStep(convertToConstructorAssignedFieldStepResult.getConstructorExpressions().iterator().next());
            success = true;
        }

        @Override
        public void visitElement(IntroduceParameterRefactoringStep introduceParameterRefactoringStep) {
            super.visitElement(introduceParameterRefactoringStep);
            findAllApplicableInterfacesWithProgressIndicator();

            if (allApplicableInterfaces.isEmpty()) {
                currentRefactoringStep = createExtractInterfaceRefactoringStep();
                success = true;
            } else {
                IntroduceParameterRefactoringStep.Result parameterResult = (IntroduceParameterRefactoringStep.Result) result;
                PsiParameter param = parameterResult.getNewParameter();

                Set<PsiType> allApplicableTypes = new HashSet<PsiType>();
                for (PsiClass clazz : allApplicableInterfaces) {
                    allApplicableTypes.add(PsiTypesUtil.getClassType(clazz));
                }

                PsiMethod method = PsiTreeUtil.getParentOfType(param, PsiMethod.class);
                PsiType suggestedType = PsiTypesUtil.getClassType(allApplicableInterfaces.iterator().next());
                if (PsiElementUtil.isAnyNullOrInvalid(method) || PsiElementUtil.isAnyTypeNullOrInvalid(suggestedType)) {
                    success = false;
                    return;
                }
                typeMigrationRefactoringStep = new TypeMigrationRefactoringStep(project, param, suggestedType, allApplicableTypes, DirectSingletonUseRefactoringImpl.this, ApplicationManager.getApplication());
                typeMigrationParameterUpdater = new TypeMigrationParameterUpdater(param, param.getType(), method);
                ApplicationManager.getApplication().addApplicationListener(typeMigrationParameterUpdater);
                currentRefactoringStep = typeMigrationRefactoringStep;
                success = true;
            }
        }

        @Override
        public void visitElement(TypeMigrationRefactoringStep typeMigrationRefactoringStep) {
            super.visitElement(typeMigrationRefactoringStep);
            ApplicationManager.getApplication().removeApplicationListener(typeMigrationParameterUpdater);
            currentRefactoringStep = null;
            success = true;
        }

        @Override
        public void visitElement(ExtractInterfaceRefactoringStep extractInterfaceRefactoringStep) {
            super.visitElement(extractInterfaceRefactoringStep);
            currentRefactoringStep = null;
            success = true;
        }

        /**
         * Checks if the step has been handled successfully.
         * @return True iff the step has been handled successfully.
         */
        public boolean hasBeenHandledSuccessfully() {
            return success;
        }

        private void findAllApplicableInterfacesWithProgressIndicator() {
            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                @Override
                public void run() {
                    allApplicableInterfaces = new ApplicableInterfacesForUsedMethodsInClassSearch(singletonClass, new LocalSearchScope(currentClass)).invoke();
                }
            }, SEARCHING_FOR_INTERFACES_MSG, false, project);
        }
    }

    private class TypeMigrationParameterUpdater extends ApplicationAdapter {
        private PsiParameter previousParameter;
        private final String parameterTypeStr;
        private final PsiMethod methodToCheck;

        private TypeMigrationParameterUpdater(PsiParameter previousParameter, PsiType parameterType, PsiMethod methodToCheck) {
            this.previousParameter = previousParameter;
            this.parameterTypeStr = parameterType.getCanonicalText();
            this.methodToCheck = methodToCheck;
        }

        @Override
        public void writeActionFinished(Object action) {
            super.writeActionFinished(action);
            if (!previousParameter.isValid()) {
                for (PsiParameter param : methodToCheck.getParameterList().getParameters()) {
                    if (param.isValid() && parameterTypeStr.equals(param.getType().getCanonicalText())) {
                        // update the element
                        typeMigrationRefactoringStep.setRootElement(param);
                        previousParameter = param;
                        break;
                    }
                }
            }
        }
    }

}
