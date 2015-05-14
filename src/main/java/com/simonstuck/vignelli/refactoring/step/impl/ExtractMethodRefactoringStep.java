package com.simonstuck.vignelli.refactoring.step.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.intellij.util.Query;
import com.simonstuck.vignelli.psi.util.EditorUtil;
import com.simonstuck.vignelli.refactoring.step.RefactoringStep;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepDelegate;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepGoalChecker;
import com.simonstuck.vignelli.refactoring.step.RefactoringStepResult;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtractMethodRefactoringStep implements RefactoringStep {

    private static final String EXTRACT_METHOD_STEP_NAME = "Extract Method";
    private final Project project;

    @NotNull
    private final Application application;
    @NotNull
    private final RefactoringStepDelegate delegate;
    private final Collection<? extends PsiElement> elementsToExtract;
    private PsiFile file;
    private String templateDescriptionPath;
    private final ExtractedMethodChecker extractedMethodChecker;

    public ExtractMethodRefactoringStep(
            @NotNull Collection<? extends PsiElement> elementsToExtract,
            @NotNull PsiFile file,
            @NotNull Project project,
            @NotNull String templateDescriptionPath,
            @NotNull Application application,
            @NotNull RefactoringStepDelegate delegate
    ) {
        this.templateDescriptionPath = templateDescriptionPath;
        this.elementsToExtract = findExtractableElementsInCommonContext(elementsToExtract);

        this.file = file;
        this.project = project;
        this.application = application;
        this.delegate = delegate;
        extractedMethodChecker = new ExtractedMethodChecker();
    }

    private Collection<? extends PsiElement> findExtractableElementsInCommonContext(Collection<? extends PsiElement> elementsToExtract) {
        if (elementsToExtract.isEmpty()) {
            return new ArrayList<PsiElement>(elementsToExtract);
        }


        PsiElement commonContext = elementsToExtract.iterator().next();

        boolean sameContext = false;
        while (!sameContext) {
            sameContext = true;
            List<PsiElement> newToExtract = new ArrayList<PsiElement>();

            Iterator<? extends PsiElement> elementIterator = elementsToExtract.iterator();
            while (elementIterator.hasNext()) {
                PsiElement element = elementIterator.next();

                if (element.getContext() == commonContext) {
                    newToExtract.add(element);
                } else {
                    sameContext = false;
                    // different context
                    //TODO: What to do if context is null?
                    if (PsiTreeUtil.isAncestor(commonContext,element.getContext(),true)) {
                        newToExtract.add(element.getParent());
                    } else {
                        commonContext = element.getContext();
                        // start over!
                        break;
                    }
                }
            }

            // if we are done though, the common context is fine and we have found
            if (!elementIterator.hasNext()) {
                elementsToExtract = newToExtract;
            }
        }
        return elementsToExtract;
    }

    @Override
    public void start() {
        application.addApplicationListener(extractedMethodChecker);
        Iterator<? extends PsiElement> elementsToExtractIterator = elementsToExtract.iterator();
        if (elementsToExtractIterator.hasNext()) {
            EditorUtil.navigateToElement(elementsToExtractIterator.next());
        }
    }

    @Override
    public void end() {
        application.removeApplicationListener(extractedMethodChecker);
    }

    @Override
    public void process() {
        PsiElement[] theElements = elementsToExtract.toArray(new PsiElement[elementsToExtract.size()]);
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, theElements, file, false);
        assert processor != null;

        ExtractMethodHandler.invokeOnElements(project, processor, file, false);
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
        Template template = new HTMLFileTemplate(IOUtil.tryReadFile(templateDescriptionPath));
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
        private final boolean success;

        public Result(PsiMethod extractedMethod, boolean success) {
            this.extractedMethod = extractedMethod;
            this.success = success;
        }

        public PsiMethod getExtractedMethod() {
            return extractedMethod;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
    }

    /**
     * This checker checks if a method has been extracted for the elements to extract.
     *
     * <p>This works approximately as follows:</p>
     * <ol>
     *     <li>Collect all methods that were originally in the class that is modified</li>
     *     <li>
     *         <span>For every change:</span>
     *         <ol>
     *             <li>Record all methods currently in the file</li>
     *             <li>Find out which ones are new with respect to the original ones</li>
     *             <li>Check if contents of a new method contains elements similar to those up for extraction</li>
     *             <li>If so, check if any of the calls to this new method have been inserted into the original method.</li>
     *         </ol>
     *     </li>
     * </ol>
     */
    private class ExtractedMethodChecker extends RefactoringStepGoalChecker {
        private Set<PsiMethod> originalCallerMethods = new HashSet<PsiMethod>();
        private Set<PsiMethod> originalMethods = new HashSet<PsiMethod>();
        private PsiClass clazz;

        public ExtractedMethodChecker() {
            super(ExtractMethodRefactoringStep.this, delegate);
            setUpOriginalCallerMethods();
            setUpOriginalMethods();
        }

        private void setUpOriginalCallerMethods() {
            for (PsiElement elementToExtract : elementsToExtract) {
                PsiMethod method = PsiTreeUtil.getParentOfType(elementToExtract, PsiMethod.class);
                if (isAnyNullOrInvalid(method)) {
                    originalCallerMethods.add(method);
                }
            }

        }

        private void setUpOriginalMethods() {
            if (!elementsToExtract.isEmpty()) {
                PsiElement sampleElement = elementsToExtract.iterator().next();
                clazz = PsiTreeUtil.getParentOfType(sampleElement, PsiClass.class);
                originalMethods = getDefinedMethods(clazz);
            }
        }

        @Override
        public RefactoringStepResult computeResult() {
            if (isAnyNullOrInvalid(clazz)) {
                return null;
            }
            Set<PsiMethod> newMethods = getDefinedMethods(clazz);
            newMethods.removeAll(originalMethods);

            for (PsiMethod newMethod : newMethods) {
                if (newMethod.isValid() && containsElementsSimilarToThoseToExtract(newMethod) && callsHaveReplacedElementsToExtract(newMethod)) {
                    return new Result(newMethod, true);
                }
            }
            return null;
        }

        private boolean containsElementsSimilarToThoseToExtract(PsiMethod newMethod) {
            PsiCodeBlock body = newMethod.getBody();
            if (isAnyNullOrInvalid(body)) {
                return false;
            }

            boolean containsAll = true;

            String textBody = body.getText();
            for (PsiElement element : elementsToExtract) {
                //FIXME: Checking for actual equivalence fails because of invalid file.
                //containsAll &= new PsiContainsChecker(correspondingMethod.getBody()).findEquivalent(element) != null;
                // Instead we are simply checking for the correct substring
                containsAll &= textBody.contains(element.getText());
            }
            return containsAll;
        }

        private boolean callsHaveReplacedElementsToExtract(PsiMethod newMethod) {
            Set<PsiMethod> callerCandidatesForBodyChange = new HashSet<PsiMethod>();
            callerCandidatesForBodyChange.addAll(originalCallerMethods);

            Query<PsiReference> referenceQuery = ReferencesSearch.search(newMethod);

            for (PsiReference reference : referenceQuery) {
                final PsiElement refElement = reference.getElement();
                if (refElement == null) {
                    continue;
                }
                final PsiElement refParentElement = refElement.getParent();
                if (refParentElement instanceof PsiMethodCallExpression) {
                    final PsiMethodCallExpression newMethodCall = (PsiMethodCallExpression) refParentElement;
                    for (PsiMethod callerCandidateForBodyChange : originalCallerMethods) {
                        if (PsiTreeUtil.isAncestor(callerCandidateForBodyChange,newMethodCall,false)) {
                            callerCandidatesForBodyChange.remove(callerCandidateForBodyChange);
                        }
                    }
                }

            }
            return callerCandidatesForBodyChange.isEmpty();
        }
    }
}
