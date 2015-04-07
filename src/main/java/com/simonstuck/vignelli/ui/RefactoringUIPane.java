package com.simonstuck.vignelli.ui;

import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.ui.description.RefactoringDescription;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class RefactoringUIPane extends JPanel {

    public RefactoringUIPane(Refactoring refactoring) {
        setLayout(new BorderLayout(5,5));

        JLabel label = new JLabel("Refactoring");
        label.setBorder(new EmptyBorder(2,2,0,0));
        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        label.setFont(Font.getFont(fontAttributes));
        add(label, BorderLayout.NORTH);

        DescriptionPane descriptionPane = new DescriptionPane();

        add(new JBScrollPane(descriptionPane));
        descriptionPane.showDescription(new RefactoringDescription(refactoring));
        validate();
        repaint();
    }
}
