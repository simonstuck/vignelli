package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCacheComponent;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCollectionListener;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.refactoring.ActiveRefactoringCollectionListener;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

class AnalysisToolJComponentWindow extends JPanel {

    private static final double RESIZE_WEIGHT = .5d;
    private static final int MIN_PROBLEM_LIST_WIDTH = 400;

    private final BatchUpdateListModel<ProblemIdentification> problemDataModel = new BatchUpdateListModel<>();
    private final BatchUpdateListModel<Refactoring> refactoringsDataModel = new BatchUpdateListModel<>();
    private final Project project;

    private ProblemDescriptionPane problemDescriptionPane;
    private ProblemListPane problemListPane;

    public AnalysisToolJComponentWindow(Project project) {
        super();
        this.project = project;

        createLayout();
        subscribeToChanges();
    }

    private void createLayout() {
        setLayout(new BorderLayout());
        JSplitPane splitPane = createSplitPane();
        add(splitPane, BorderLayout.CENTER);
    }

    private JSplitPane createSplitPane() {
        problemDescriptionPane = createProblemDescriptionPane();
        JScrollPane scrollDescriptionPane = new JBScrollPane(problemDescriptionPane);
        JScrollPane scrollListPane = createProblemListPane();

        JSplitPane listSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ActiveRefactoringsListPane(project), scrollListPane);
        listSplitPane.setSize(getSize());
//        listSplitPane.setResizeWeight(RESIZE_WEIGHT);




        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listSplitPane, scrollDescriptionPane);
        splitPane.setSize(getSize());
        splitPane.setResizeWeight(RESIZE_WEIGHT);

        return splitPane;
    }

    private JScrollPane createProblemListPane() {
        problemListPane = new ProblemListPane(problemDataModel);
        problemListPane.getSelectionModel().addListSelectionListener(event -> {
            synchronized (problemDataModel) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!event.getValueIsAdjusting()) {
                        int index = problemListPane.getSelectedIndex();
                        if (index == -1) {
                            problemDescriptionPane.showDefault();
                        } else {
                            ProblemIdentification id = problemDataModel.getElementAt(index);
                            problemDescriptionPane.showDescription(id);
                        }
                    }
                });
            }
        });

        JScrollPane scrollPane = new JBScrollPane(problemListPane);
        scrollPane.setMinimumSize(new Dimension(MIN_PROBLEM_LIST_WIDTH, getHeight()));
        return scrollPane;
    }

    private ProblemDescriptionPane createProblemDescriptionPane() {
        return new ProblemDescriptionPane();
    }

    private void subscribeToChanges() {
        MessageBusConnection connection = project.getMessageBus().connect();
        UIProblemIdentificationCollectionListener listener = new UIProblemIdentificationCollectionListener();
        connection.subscribe(ProblemIdentificationCacheComponent.INSPECTION_IDENTIFICATION_TOPIC, listener);
        initDataStoreWithExistingProblems(listener);

        ActiveRefactoringCollectionListener refactoringsListener = new MyActiveRefactoringCollectionListener();
        connection.subscribe(RefactoringEngineComponent.ACTIVE_REFACTORINGS_TOPIC, refactoringsListener);
    }

    private void initDataStoreWithExistingProblems(UIProblemIdentificationCollectionListener listener) {
        ProblemIdentificationCacheComponent problemIdentificationCacheComponent = project.getComponent(ProblemIdentificationCacheComponent.class);
        listener.accept(problemIdentificationCacheComponent.selectedFileProblems());
    }

    private class MyActiveRefactoringCollectionListener implements ActiveRefactoringCollectionListener {
        @Override
        public void accept(Collection<Refactoring> refactorings) {
            refactoringsDataModel.batchUpdateContents(refactorings);
        }
    }

    private class UIProblemIdentificationCollectionListener implements ProblemIdentificationCollectionListener {
        @Override
        public void accept(Collection<ProblemIdentification> identifications) {
            ApplicationManager.getApplication().invokeLater(() -> {
                problemDataModel.batchUpdateContents(identifications);
                problemListPane.invalidate();
                problemListPane.repaint();
            });
        }
    }
}
