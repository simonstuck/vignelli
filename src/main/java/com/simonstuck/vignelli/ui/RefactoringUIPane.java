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

public class RefactoringUIPane extends JPanel implements DescriptionPane.Delegate {

    public static final String PANE_TITLE = "Refactoring";
    
    private final RefactoringDescription refactoringDescription;
    private final JBScrollPane scrollPane;

    public RefactoringUIPane(Refactoring refactoring) {
        setLayout(new BorderLayout(5,5));

        JLabel label = new JLabel(PANE_TITLE);
        label.setBorder(new EmptyBorder(2,2,0,0));
        Map<TextAttribute, Object> fontAttributes = new HashMap<TextAttribute, Object>();
        fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        label.setFont(Font.getFont(fontAttributes));
        add(label, BorderLayout.NORTH);

        DescriptionPane descriptionPane = new DescriptionPane(this);

        scrollPane = new JBScrollPane(descriptionPane);
        add(scrollPane);
        refactoringDescription = new RefactoringDescription(refactoring);
        descriptionPane.showDescription(refactoringDescription);
        validate();
        repaint();
    }

    public void tearDown() {
        refactoringDescription.tearDown();
    }

    /**
     * Scrolls to the top of the scroll pane
     */
    private void scrollToTop() {
        scrollPane.getVerticalScrollBar().setValue(0);
        scrollPane.getHorizontalScrollBar().setValue(0);
    }

    @Override
    public void didUpdateDescriptionPane(DescriptionPane descriptionPane) {
        scrollToTop();
    }
}
