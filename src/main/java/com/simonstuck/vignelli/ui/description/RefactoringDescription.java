package com.simonstuck.vignelli.ui.description;

import com.intellij.openapi.diagnostic.Logger;
import com.simonstuck.vignelli.refactoring.Refactoring;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.event.HyperlinkEvent;

public class RefactoringDescription extends Description implements Observer {

    private static final Logger LOG = Logger.getInstance(RefactoringDescription.class.getName());
    private final Refactoring refactoring;

    public RefactoringDescription(Refactoring refactoring) {
        this.refactoring = refactoring;
        this.refactoring.addObserver(this);
    }

    @Override
    public String render() {
        Template template = new HTMLFileTemplate(refactoring.template());
        HashMap<String, Object> contentMap = new HashMap<String, Object>();

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
        refactoring.nextStep();
        setChanged();
        notifyObservers(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable == refactoring) {
            setChanged();
            notifyObservers(this);
        }
    }

    public void tearDown() {
        this.refactoring.deleteObserver(this);
    }
}
