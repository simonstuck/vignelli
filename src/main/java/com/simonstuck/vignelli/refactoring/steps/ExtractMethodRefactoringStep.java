package com.simonstuck.vignelli.refactoring.steps;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;

import java.util.HashMap;
import java.util.Map;

public class ExtractMethodRefactoringStep {

    private final Project project;
    private final PsiElement[] elementsToExtract;
    private PsiFile file;

    public ExtractMethodRefactoringStep(PsiElement[] elementsToExtract, PsiFile file, Project project) {
        this.elementsToExtract = elementsToExtract;
        this.file = file;
        this.project = project;
    }

    public Result process() {
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elementsToExtract, file, false);
        assert processor != null;

        ExtractMethodHandler.invokeOnElements(project, processor, file, false);
        return new Result(processor.getExtractedMethod());
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
