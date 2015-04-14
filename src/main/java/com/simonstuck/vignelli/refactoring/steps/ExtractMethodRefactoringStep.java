package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractMethodRefactoringStep {

    private static final String EXTRACT_METHOD_STEP_NAME = "Extract Method";
    private final Project project;
    private final Collection<? extends PsiElement> elementsToExtract;
    private PsiFile file;

    public ExtractMethodRefactoringStep(Collection<? extends PsiElement> elementsToExtract, PsiFile file, Project project) {
        this.elementsToExtract = elementsToExtract;
        this.file = file;
        this.project = project;
    }

    public Result process() {
        PsiElement[] theElements = elementsToExtract.toArray(new PsiElement[elementsToExtract.size()]);
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, theElements, file, false);
        assert processor != null;

        ExtractMethodHandler.invokeOnElements(project, processor, file, false);
        return new Result(processor.getExtractedMethod());
    }

    public void describeStep(Map<String, Object> templateValues) {
        templateValues.put("nextStepDescription", getDescription());
        templateValues.put("nextStepName", EXTRACT_METHOD_STEP_NAME);
    }

    private String getDescription() {
        Template template = new HTMLFileTemplate(template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        List<String> strElementsToExtract = new ArrayList<String>(elementsToExtract.size());
        for (PsiElement element : elementsToExtract) {
            strElementsToExtract.add(element.getText());
        }
        contentMap.put("elementsToExtract", strElementsToExtract);

        return template.render(contentMap);
    }

    private String template() {
        try {
            return IOUtils.readFile("descriptionTemplates/extractMethodStepDescription.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public final class Result {
        private final PsiMethod extractedMethod;

        public Result(PsiMethod extractedMethod) {
            this.extractedMethod = extractedMethod;
        }

        public PsiMethod getExtractedMethod() {
            return extractedMethod;
        }
    }
}
