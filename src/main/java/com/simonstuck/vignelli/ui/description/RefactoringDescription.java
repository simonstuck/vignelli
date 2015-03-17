package com.simonstuck.vignelli.ui.description;

import com.intellij.openapi.diagnostic.Logger;
import com.simonstuck.vignelli.refactoring.Refactoring;

import java.util.HashMap;
import java.util.Map;
import javax.swing.event.HyperlinkEvent;

public class RefactoringDescription extends Description {

    private static final Logger LOG = Logger.getInstance(RefactoringDescription.class.getName());
    private final Refactoring refactoring;

    public RefactoringDescription(Refactoring refactoring) {
        this.refactoring = refactoring;
    }

    @Override
    public String render() {
        Template template = new HTMLFileTemplate(refactoring.template());
        Map<String, Object> contentMap = new HashMap<>();

        refactoring.fillTemplateValues(contentMap);
        return template.render(contentMap);
    }

    @Override
    public void handleVignelliLinkEvent(HyperlinkEvent event) {
        LOG.info("Refactoring next step event sent: " + event);
        if (event.getDescription().endsWith("nextStep")) {
            performNextStep();
        } else if (event.getDescription().endsWith("complete")) {
            completeRefactoring();
        }
    }

    private void completeRefactoring() {
        setChanged();
        notifyObservers();
        refactoring.complete();
    }

    private void performNextStep() {
        try {
            refactoring.nextStep();
            setChanged();
            notifyObservers(this);
        } catch (NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
