package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCacheComponent;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCollectionListener;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.ui.description.ProblemIdentificationDescription;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

class ProblemListPane extends JPanel {
    private final ProblemTableModel model;
    private final JBTable tablePane;
    private final ProblemUIPane delegate;
    private ProblemIdentification currentIdentification;

    public ProblemListPane(Project project, ProblemUIPane delegate) {
        this.delegate = delegate;
        setLayout(new BorderLayout(5,5));
        JLabel label = new JLabel("Active Problems");
        label.setBorder(new EmptyBorder(2,2,0,0));
        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.WEIGHT,TextAttribute.WEIGHT_BOLD);
        label.setFont(Font.getFont(fontAttributes));
        add(label, BorderLayout.NORTH);

        model = new ProblemTableModel();
        tablePane = new JBTable(model);
        tablePane.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePane.setShowGrid(false);
        tablePane.setShowHorizontalLines(false);
        tablePane.setShowVerticalLines(false);
        tablePane.setRowMargin(0);
        tablePane.setIntercellSpacing(new Dimension(0, 0));
        tablePane.setFillsViewportHeight(true);
        tablePane.getColumnModel().getColumn(0).setMaxWidth(30);
        tablePane.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tablePane.doLayout();

        tablePane.getSelectionModel().addListSelectionListener(event -> ApplicationManager.getApplication().invokeLater(() -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedProblemDescription(delegate);
            }
        }));

        UIProblemIdentificationCollectionListener listener = new UIProblemIdentificationCollectionListener();
        project.getMessageBus().connect().subscribe(ProblemIdentificationCacheComponent.INSPECTION_IDENTIFICATION_TOPIC, listener);
        ProblemIdentificationCacheComponent component = project.getComponent(ProblemIdentificationCacheComponent.class);
        listener.accept(component.selectedFileProblems());

        Component scrollListPane = new JBScrollPane(tablePane);
        add(scrollListPane, BorderLayout.CENTER);
    }

    private List<ProblemIdentification> sortProblems(Collection<ProblemIdentification> problemIdentifications) {
        List<ProblemIdentification> result = new ArrayList<>(problemIdentifications);
        Collections.sort(result, (id1, id2) -> Comparing.compare(id1.getProblemDescriptor().getLineNumber(), id2.getProblemDescriptor().getLineNumber()));
        return result;
    }

    private void showSelectedProblemDescription(ProblemUIPane delegate) {
        int index = tablePane.getSelectedRow();
        if (index > -1) {
            currentIdentification = model.getProblemDataAt(index);
            delegate.showDescription(new ProblemIdentificationDescription(currentIdentification));
        }
    }

    private void tryShowingFirstAvailableProblemDescription() {
        if (!model.isEmpty()) {
            tablePane.setRowSelectionInterval(0,0);
        } else {
            delegate.showDescription(null);
            tablePane.clearSelection();
        }
    }

    private class UIProblemIdentificationCollectionListener implements ProblemIdentificationCollectionListener {
        @Override
        public void accept(Collection<ProblemIdentification> identifications) {
            ApplicationManager.getApplication().invokeLater(() -> {
                model.batchUpdateContents(sortProblems(identifications));

                if (!model.contains(currentIdentification)) {
                    tryShowingFirstAvailableProblemDescription();
                }
                invalidate();
                repaint();
            });
        }
    }

}
