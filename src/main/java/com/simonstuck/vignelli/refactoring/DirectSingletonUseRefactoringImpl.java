package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.simonstuck.vignelli.utils.IOUtils;

import java.util.Map;

public class DirectSingletonUseRefactoringImpl implements Refactoring {

    private static final String DESCRIPTION_TEMPLATE = "descriptionTemplates/directSingletonUseRefactoring.html";
    private final RefactoringTracker tracker;

    public DirectSingletonUseRefactoringImpl(PsiMethodCallExpression getInstanceElement, RefactoringTracker tracker, Project project, PsiFile file) {

        this.tracker = tracker;
    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

    @Override
    public void nextStep() throws NoSuchMethodException {

    }

    @Override
    public void fillTemplateValues(Map<String, Object> templateValues) {

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
