package com.simonstuck.vignelli.ui.analysis;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCache;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCollectionListener;
import com.simonstuck.vignelli.inspection.VignelliLocalInspectionTool;
import com.simonstuck.vignelli.inspection.identification.Identification;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;

import javax.swing.*;
import java.awt.*;

public class AnalysisToolJComponentWindow extends JPanel {
    private static final double RESIZE_WEIGHT = .5d;
    private static final int MIN_PROBLEM_LIST_WIDTH = 400;

    private final IdentificationCollectionListModel dataModel = new IdentificationCollectionListModel();

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
        JScrollPane scrollPane = createProblemListPane();
        JComponent problemDescriptionPane = createProblemDescriptionPane();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, problemDescriptionPane);
        splitPane.setSize(getSize());
        splitPane.setResizeWeight(RESIZE_WEIGHT);

        return splitPane;
    }

    private JScrollPane createProblemListPane() {
        JComponent problemListPane = new ProblemListPane(dataModel);
        JScrollPane scrollPane = new JBScrollPane(problemListPane);
        scrollPane.setMinimumSize(new Dimension(MIN_PROBLEM_LIST_WIDTH, getHeight()));
        return scrollPane;
    }

    private JComponent createProblemDescriptionPane() {
        return new ProblemDescriptionPane();
    }

    private void subscribeToChanges(Project project) {
        MessageBusConnection connection = project.getMessageBus().connect();
        UIProblemIdentificationCollectionListener listener = new UIProblemIdentificationCollectionListener();
        connection.subscribe(VignelliLocalInspectionTool.INSPECTION_IDENTIFICATION_TOPIC, listener);

        initDataStoreWithExistingProblems(project, listener);
    }

    private void initDataStoreWithExistingProblems(Project project, UIProblemIdentificationCollectionListener listener) {
        ProblemIdentificationCache problemIdentificationCache = project.getComponent(ProblemIdentificationCache.class);
        listener.accept(problemIdentificationCache.getAllProblems());
    }

    private class UIProblemIdentificationCollectionListener implements ProblemIdentificationCollectionListener {
        @Override
        public void accept(IdentificationCollection<? extends Identification> identifications) {
            for (Identification id : identifications) {
                if (!dataModel.contains(id)) {
                    dataModel.add(id);
                }
            }
        }
    }
}
