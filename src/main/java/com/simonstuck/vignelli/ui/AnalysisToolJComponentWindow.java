package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.simonstuck.vignelli.refactoring.ActiveRefactoringCollectionListener;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;

import java.awt.BorderLayout;
import java.util.Collection;
import javax.swing.JPanel;

class AnalysisToolJComponentWindow extends JPanel {

    private static final Logger LOG = Logger.getInstance(AnalysisToolJComponentWindow.class.getName());

    private final ProblemUIPane problemUIPane;

    private RefactoringUIPane currentRefactoringUIPane = null;

    public AnalysisToolJComponentWindow(Project project) {
        super();
        this.problemUIPane = new ProblemUIPane(project);

        setLayout(new BorderLayout());

        // subscribe to refactorings
        UIActiveRefactoringCollectionListener listener = new UIActiveRefactoringCollectionListener();
        project.getMessageBus().connect().subscribe(RefactoringEngineComponent.ACTIVE_REFACTORINGS_TOPIC, listener);
        RefactoringEngineComponent refactoringEngineComponent = project.getComponent(RefactoringEngineComponent.class);
        listener.consume(refactoringEngineComponent.activeRefactorings());

        showProblemUI();
    }

    private void showProblemUI() {
        tearDownCurrentRefactoringUIPaneIfNecessary();
        this.removeAll();
        this.add(problemUIPane);
        validate();
        repaint();
    }

    private void showRefactoringUI(Refactoring refactoring) {
        tearDownCurrentRefactoringUIPaneIfNecessary();
        this.removeAll();
        currentRefactoringUIPane = new RefactoringUIPane(refactoring);
        this.add(currentRefactoringUIPane);
        validate();
        repaint();
    }

    private void tearDownCurrentRefactoringUIPaneIfNecessary() {
        if (currentRefactoringUIPane != null) {
            currentRefactoringUIPane.tearDown();
        }
        currentRefactoringUIPane = null;
    }

    private class UIActiveRefactoringCollectionListener implements ActiveRefactoringCollectionListener {

        @Override
        public void consume(final Collection<Refactoring> refactorings) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    LOG.info("new refactoring: " + refactorings);
                    if (refactorings.isEmpty()) {
                        showProblemUI();
                    } else {
                        // We only allow one refactoring in this UI to be active
                        Refactoring refactoring = refactorings.iterator().next();
                        showRefactoringUI(refactoring);
                    }
                }
            });
        }
    }
}
