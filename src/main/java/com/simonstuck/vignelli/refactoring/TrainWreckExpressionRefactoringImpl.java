package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.simonstuck.vignelli.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class TrainWreckExpressionRefactoringImpl implements Refactoring {

    private static final Logger LOG = Logger.getInstance(TrainWreckExpressionRefactoringImpl.class.getName());
    private final PsiElement trainWreckElement;
    private final RefactoringTracker tracker;

    public TrainWreckExpressionRefactoringImpl(PsiElement trainWreckElement, RefactoringTracker tracker) {

        this.trainWreckElement = trainWreckElement;
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
    public int totalSteps() {
        return 0;
    }

    @Override
    public int currentStepNumber() {
        return 0;
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
        try {
            return IOUtils.readFile(getClass().getResource("/descriptionTemplates/trainWreckRefactoring.html").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainWreckExpressionRefactoringImpl that = (TrainWreckExpressionRefactoringImpl) o;

        return !(tracker != null ? !tracker.equals(that.tracker) : that.tracker != null) && !(trainWreckElement != null ? !trainWreckElement.equals(that.trainWreckElement) : that.trainWreckElement != null);

    }

    @Override
    public int hashCode() {
        int result = trainWreckElement != null ? trainWreckElement.hashCode() : 0;
        result = 31 * result + (tracker != null ? tracker.hashCode() : 0);
        return result;
    }
}
