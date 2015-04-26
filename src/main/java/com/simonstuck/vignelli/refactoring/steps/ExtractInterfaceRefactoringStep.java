package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.extractInterface.ExtractInterfaceHandler;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ExtractInterfaceRefactoringStep implements RefactoringStep {
    private static final String STEP_NAME = "Extract Interface for Singleton and Use it Wherever Possible";
    private static final String TEMPLATE_PATH = "descriptionTemplates/extractInterfaceStepDescription.html";
    private final Project project;
    private final PsiClass clazz;
    private final PsiClass currentClass;

    public ExtractInterfaceRefactoringStep(@NotNull Project project, @NotNull PsiClass clazz, @NotNull PsiClass currentClass) {
        this.project = project;
        this.clazz = clazz;
        this.currentClass = currentClass;
    }

    @Override
    public void startListeningForGoal() {

    }

    @Override
    public void endListeningForGoal() {
        
    }

    @Override
    public Result process() {
        ExtractInterfaceHandler handler = new ExtractInterfaceHandler();
        PsiElement[] elements = new PsiElement[] { clazz };
        handler.invoke(project, elements, null);
        return new Result();
    }

    @Override
    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(IOUtils.tryReadFile(TEMPLATE_PATH));
        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("singletonClass", clazz.getName());
        content.put("thisClass", currentClass.getName());
        return template.render(content);
    }

    public static final class Result implements RefactoringStepResult {

        @Override
        public boolean isSuccess() {
            return true;
        }
    }
}
