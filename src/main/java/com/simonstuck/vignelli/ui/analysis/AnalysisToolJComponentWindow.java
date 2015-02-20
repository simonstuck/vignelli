package com.simonstuck.vignelli.ui.analysis;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

public class AnalysisToolJComponentWindow extends JPanel {
    private static final double RESIZE_WEIGHT = .5d;
    private static final int MIN_PROBLEM_LIST_WIDTH = 400;

    private JSplitPane splitPane;

    public AnalysisToolJComponentWindow() {
        super();
        setLayout(new BorderLayout());


        DefaultListModel<String> dataModel = new DefaultListModel<String>();
        for (int i = 0; i < 200; i++) {
            dataModel.addElement("Element --> " + i);
        }
        JComponent problemListPane = new ProblemListPane(dataModel);
        JScrollPane scrollPane = new JBScrollPane(problemListPane);
        scrollPane.setMinimumSize(new Dimension(MIN_PROBLEM_LIST_WIDTH, getHeight()));

        problemListPane.setOpaque(true);
        problemListPane.setBackground(JBColor.CYAN);

        JComponent problemDescriptionPane = new ProblemDescriptionPane();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, problemDescriptionPane);
        splitPane.setSize(getSize());
        splitPane.setResizeWeight(RESIZE_WEIGHT);

        add(splitPane, BorderLayout.CENTER);
    }
}
