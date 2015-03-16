package com.simonstuck.vignelli.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.ui.description.Description;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

class AnalysisToolJComponentWindow extends JPanel {

    private static final double RESIZE_WEIGHT = .5d;
    private static final int MIN_PROBLEM_LIST_WIDTH = 400;

    private final Project project;

    private DescriptionPane descriptionPane;
    private ProblemListPane problemListPane;
    private ActiveRefactoringsListPane refactoringListPane;

    public AnalysisToolJComponentWindow(Project project) {
        super();
        this.project = project;

        createLayout();
    }

    private void createLayout() {
        setLayout(new BorderLayout());
        JSplitPane splitPane = createSplitPane();
        add(splitPane, BorderLayout.CENTER);
    }

    private JSplitPane createSplitPane() {
        descriptionPane = createProblemDescriptionPane();
        JScrollPane scrollDescriptionPane = new JBScrollPane(descriptionPane);
        problemListPane = new ProblemListPane(project, this);

        refactoringListPane = new ActiveRefactoringsListPane(project, this);
        JSplitPane listSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, refactoringListPane, problemListPane);
        listSplitPane.setSize(getSize());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listSplitPane, scrollDescriptionPane);
        splitPane.setSize(getSize());
        splitPane.setResizeWeight(RESIZE_WEIGHT);

        return splitPane;
    }

    private DescriptionPane createProblemDescriptionPane() {
        return new DescriptionPane();
    }


    public void showDescription(Description description) {
        if (description == null) {
            descriptionPane.showDefault();
        } else {
            descriptionPane.showDescription(description);
        }
    }

    public void deselectOthers(JPanel newSelectionOwner) {
        if (newSelectionOwner == refactoringListPane) {
            problemListPane.clearSelection();
        } else {
            refactoringListPane.clearSelection();
        }
    }
}
