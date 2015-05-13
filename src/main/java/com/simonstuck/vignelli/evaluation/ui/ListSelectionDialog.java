package com.simonstuck.vignelli.evaluation.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ListSelectionDialog extends DialogWrapper {

    private final JBList jbList;
    private final JPanel mainPanel;

    public ListSelectionDialog(@Nullable Project project, @NotNull String[] selectableValues) {
        super(project);
        jbList = new JBList();
        jbList.setSelectionModel(new ToggleListSelectionModel());
        DefaultListModel listModel = new DefaultListModel();
        for (String selectableValue : selectableValues) {
            listModel.addElement(selectableValue);
        }
        jbList.setModel(listModel);
        mainPanel = new JPanel(new BorderLayout(15,0));
        mainPanel.add(jbList, BorderLayout.CENTER);

        setOKActionEnabled(true);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @NotNull
    public String[] getSelectedValues() {
        return Arrays.copyOf(jbList.getSelectedValues(), jbList.getSelectedValues().length, String[].class);
    }

}
