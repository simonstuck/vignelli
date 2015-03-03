package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCacheComponent;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCollectionListener;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import javax.swing.*;

class AnalysisToolJComponentWindow extends JPanel {

    private static final double RESIZE_WEIGHT = .5d;
    private static final int MIN_PROBLEM_LIST_WIDTH = 400;

    private final ProblemIdentificationCollectionListModel dataModel = new ProblemIdentificationCollectionListModel();
    private ProblemDescriptionPane problemDescriptionPane;

    public AnalysisToolJComponentWindow(Project project) {
        super();

        subscribeToChanges(project);
        createLayout();
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollListPane, scrollDescriptionPane);
        splitPane.setSize(getSize());
        splitPane.setResizeWeight(RESIZE_WEIGHT);

        return splitPane;
    }

    private JScrollPane createProblemListPane() {
        final ProblemListPane problemListPane = new ProblemListPane(dataModel);
        problemListPane.getSelectionModel().addListSelectionListener(event -> {
            synchronized (dataModel) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!event.getValueIsAdjusting()) {
                        int index = problemListPane.getSelectedIndex();
                        if (index == -1) {
                            problemDescriptionPane.showDefault();
                        } else {
                            ProblemIdentification id = dataModel.getElementAt(index);
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

    private void subscribeToChanges(Project project) {
        MessageBusConnection connection = project.getMessageBus().connect();
        UIProblemIdentificationCollectionListener listener = new UIProblemIdentificationCollectionListener();
        connection.subscribe(ProblemIdentificationCacheComponent.INSPECTION_IDENTIFICATION_TOPIC, listener);

        initDataStoreWithExistingProblems(project, listener);
    }

    private void initDataStoreWithExistingProblems(Project project, UIProblemIdentificationCollectionListener listener) {
        ProblemIdentificationCacheComponent problemIdentificationCacheComponent = project.getComponent(ProblemIdentificationCacheComponent.class);
        listener.accept(problemIdentificationCacheComponent.selectedFileProblems());
    }

    private class UIProblemIdentificationCollectionListener implements ProblemIdentificationCollectionListener {
        @Override
        public void accept(Collection<ProblemIdentification> identifications) {
            ApplicationManager.getApplication().invokeLater(() -> {
                dataModel.replaceWithNewContents(identifications);
            });
        }
    }
}
