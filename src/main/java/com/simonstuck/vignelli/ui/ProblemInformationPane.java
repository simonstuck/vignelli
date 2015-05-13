package com.simonstuck.vignelli.ui;

import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.ui.description.Description;

import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

class ProblemInformationPane extends JPanel implements InformationPane {
    @NotNull
    private final DescriptionPane descriptionPane;

    public ProblemInformationPane(@NotNull DescriptionPane descriptionPane) {
        this.descriptionPane = descriptionPane;
        JScrollPane scrollDescriptionPane = new JBScrollPane(descriptionPane);
        setLayout(new BorderLayout());
        add(scrollDescriptionPane, BorderLayout.CENTER);
    }

    @Override
    public void showDescription(Description description) {
        descriptionPane.showDescription(description);
    }

    @Override
    public void showDefault() {
        descriptionPane.showDefault();
    }
}
