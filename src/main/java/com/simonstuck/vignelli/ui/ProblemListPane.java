package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCacheComponent;
import com.simonstuck.vignelli.inspection.ProblemIdentificationCollectionListener;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.ui.description.ProblemIdentificationDescription;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class ProblemListPane extends JPanel {
    private final BatchUpdateListModel<ProblemIdentification> model;
    private final JBList listPane;
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

        listPane = new JBList();

        model = new BatchUpdateListModel<>();
        listPane.setModel(model);

        listPane.getSelectionModel().addListSelectionListener(event -> ApplicationManager.getApplication().invokeLater(() -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedProblemDescription(delegate);
            }
        }));

        UIProblemIdentificationCollectionListener listener = new UIProblemIdentificationCollectionListener();
        project.getMessageBus().connect().subscribe(ProblemIdentificationCacheComponent.INSPECTION_IDENTIFICATION_TOPIC, listener);
        ProblemIdentificationCacheComponent component = project.getComponent(ProblemIdentificationCacheComponent.class);
        listener.accept(component.selectedFileProblems());

        Component scrollListPane = new JBScrollPane(listPane);
        add(scrollListPane, BorderLayout.CENTER);
    }

    private void showSelectedProblemDescription(ProblemUIPane delegate) {
        int index = listPane.getSelectedIndex();
        if (index > -1) {
            currentIdentification = model.getElementAt(index);
            delegate.showDescription(new ProblemIdentificationDescription(currentIdentification));
        }
    }

    private void tryShowingFirstAvailableProblemDescription() {
        if (!model.contains(currentIdentification)) {
            if (!model.isEmpty()) {
                listPane.setSelectedIndex(0);
            } else {
                delegate.showDescription(null);
            }
        }
    }

    private class UIProblemIdentificationCollectionListener implements ProblemIdentificationCollectionListener {
        @Override
        public void accept(Collection<ProblemIdentification> identifications) {
            ApplicationManager.getApplication().invokeLater(() -> {
                model.batchUpdateContents(identifications);
                tryShowingFirstAvailableProblemDescription();
                invalidate();
                repaint();
            });
        }
    }

}
