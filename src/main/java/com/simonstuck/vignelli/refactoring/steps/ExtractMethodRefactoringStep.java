package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.simonstuck.vignelli.psi.PsiContainsChecker;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtractMethodRefactoringStep implements RefactoringStep {

    private static final String EXTRACT_METHOD_STEP_NAME = "Extract Method";
    private final Project project;

    @NotNull
    private final PsiManager psiManager;
    @Nullable
    private final RefactoringStepDelegate delegate;
    private final Collection<? extends PsiElement> elementsToExtract;
    private PsiFile file;
    private String templateDescriptionPath;
    private final PsiTreeChangeListener extractedMethodChecker;

    public ExtractMethodRefactoringStep(
            @NotNull Collection<? extends PsiElement> elementsToExtract,
            @NotNull PsiFile file,
            @NotNull Project project,
            @NotNull String templateDescriptionPath,
            @NotNull PsiManager psiManager,
            @Nullable RefactoringStepDelegate delegate
    ) {
        this.templateDescriptionPath = templateDescriptionPath;
        this.elementsToExtract = elementsToExtract;
        this.file = file;
        this.project = project;
        this.psiManager = psiManager;
        this.delegate = delegate;
        extractedMethodChecker = new ExtractedMethodChecker();
    }

    @Override
    public void startListeningForGoal() {
        psiManager.addPsiTreeChangeListener(extractedMethodChecker);
    }

    @Override
    public void endListeningForGoal() {
        psiManager.removePsiTreeChangeListener(extractedMethodChecker);
    }

    @Override
    public Result process() {
        PsiElement[] theElements = elementsToExtract.toArray(new PsiElement[elementsToExtract.size()]);
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, theElements, file, false);
        assert processor != null;

        ExtractMethodHandler.invokeOnElements(project, processor, file, false);
        return new Result(processor.getExtractedMethod());
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put(STEP_DESCRIPTION_TEMPLATE_KEY, getDescription());
        templateValues.put(STEP_NAME_TEMPLATE_KEY, EXTRACT_METHOD_STEP_NAME);
    }

    /**
     * Gets the HTML description of this refactoring step.
     * @return The HTML description of this refactoring step.
     */
    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtils.tryReadFile(templateDescriptionPath));
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        List<String> strElementsToExtract = new ArrayList<String>(elementsToExtract.size());
        for (PsiElement element : elementsToExtract) {
            strElementsToExtract.add(element.getText());
        }
        contentMap.put("elementsToExtract", strElementsToExtract);

        return template.render(contentMap);
    }

    public static final class Result implements RefactoringStepResult {
        private final PsiMethod extractedMethod;

        public Result(PsiMethod extractedMethod) {
            this.extractedMethod = extractedMethod;
        }

        public PsiMethod getExtractedMethod() {
            return extractedMethod;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private class ExtractedMethodChecker extends PsiTreeChangeAdapter {
        private PsiMethod foundMethod = null;
        private Set<String> newMethodCallNames = new HashSet<String>();

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            super.childAdded(event);

            if (event.getChild() instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) event.getChild();
                if (containsAllStatementstoExtract(method)) {
                    foundMethod = method;
                }
            }
            checkMe();
        }

        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
            super.childrenChanged(event);

            doAThing(event);
            checkMe();
        }

        @Override
        public void childReplaced(@NotNull PsiTreeChangeEvent event) {
            super.childReplaced(event);
            doAThing(event);
            checkMe();
        }

        private void doAThing(PsiTreeChangeEvent event) {
            @SuppressWarnings("unchecked")
            Collection<PsiMethodCallExpression> addedMethodCalls = PsiTreeUtil.collectElementsOfType(event.getChild(), PsiMethodCallExpression.class);
            for (PsiMethodCallExpression addedMethodCall : addedMethodCalls) {
                newMethodCallNames.add(addedMethodCall.getMethodExpression().getText());
            }

            checkMe();
        }

        private void checkMe() {
            if (delegate != null && foundMethod != null && newMethodCallNames.contains(foundMethod.getName())) {
                delegate.didFinishRefactoringStep(ExtractMethodRefactoringStep.this, new Result(foundMethod));
            }
        }

        private boolean containsAllStatementstoExtract(PsiMethod correspondingMethod) {
            boolean containsAll = true;
            for (PsiElement element : elementsToExtract) {
                //FIXME: Checking for actual equivalence fails because of invalid file.
                containsAll &= correspondingMethod.getBody().getText().contains(element.getText());
//                containsAll &= new PsiContainsChecker(correspondingMethod.getBody()).findEquivalent(element) != null;
            }
            return containsAll;
        }
    }
}
