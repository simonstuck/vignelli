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

    private DescriptionPane descriptionPane;

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
        descriptionPane = createProblemDescriptionPane();
        JScrollPane scrollDescriptionPane = new JBScrollPane(descriptionPane);
        ProblemListPane problemListPane = new ProblemListPane(project, this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, problemListPane, scrollDescriptionPane);
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
}
