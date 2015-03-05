package com.simonstuck.vignelli.ui;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ActiveRefactoringsListPane extends JPanel {
    public ActiveRefactoringsListPane() {
        setLayout(new BorderLayout(5,5));

        JLabel label = new JLabel("Active Refactorings");
        label.setBorder(new EmptyBorder(2,2,0,0));
        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.WEIGHT,TextAttribute.WEIGHT_BOLD);
        label.setFont(Font.getFont(fontAttributes));
        add(label, BorderLayout.NORTH);

        JBList listPane = new JBList();

        BatchUpdateListModel<Object> model = new BatchUpdateListModel<>();
        model.addElement("Hello world");
        model.addElement("Yeah");
        listPane.setModel(model);
        Component scrollListPane = new JBScrollPane(listPane);
        add(scrollListPane, BorderLayout.CENTER);
    }
}
