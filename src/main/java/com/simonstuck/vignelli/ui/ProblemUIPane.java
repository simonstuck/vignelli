package com.simonstuck.vignelli.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.ui.description.Description;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class ProblemUIPane extends JPanel {

    private static final double RESIZE_WEIGHT = .5d;

    private final Project project;

    private ProblemInformationPane problemInformationPane;

    public ProblemUIPane(Project project) {
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
        problemInformationPane = new ProblemInformationPane(new DescriptionPane());
        ProblemListPane problemListPane = new ProblemListPane(project, this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, problemListPane, problemInformationPane);
        splitPane.setSize(getSize());
        splitPane.setResizeWeight(RESIZE_WEIGHT);

        return splitPane;
    }


    public void showDescription(Description description) {
        if (description == null) {
            problemInformationPane.showDefault();
        } else {
            problemInformationPane.showDescription(description);
        }
    }
}
