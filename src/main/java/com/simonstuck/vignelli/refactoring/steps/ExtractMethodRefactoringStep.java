package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;

import java.util.HashMap;
import java.util.Map;

public class ExtractMethodRefactoringStep implements RefactoringStep {

    private final Project project;
    private final PsiElement[] elementsToExtract;
    private PsiFile file;

    public ExtractMethodRefactoringStep(Map<String, Object> arguments) {
        project = (Project) arguments.get("project");
        elementsToExtract = (PsiElement[]) arguments.get("elementsToExtract");
        file = (PsiFile) arguments.get("file");
    }

    @Override
    public Map<String, Object> process() {

        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elementsToExtract, file, false);
        Map<String, Object> results = new HashMap<>();
        if (processor != null) {
            ExtractMethodHandler.invokeOnElements(project, processor, file, false);
            results.put("extractedMethod", processor.getExtractedMethod());
        }
        return results;
    }
}
