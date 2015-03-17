package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.refactoring.ActiveRefactoringCollectionListener;
import com.simonstuck.vignelli.refactoring.Refactoring;
import com.simonstuck.vignelli.refactoring.RefactoringEngineComponent;
import com.simonstuck.vignelli.ui.description.Description;
import com.simonstuck.vignelli.ui.description.RefactoringDescription;

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

public class ActiveRefactoringsListPane extends JPanel {

    private final BatchUpdateListModel<Refactoring> model;
    private final JBList listPane;
    private final AnalysisToolJComponentWindow delegate;
    private Refactoring currentlyShownElement;

    public ActiveRefactoringsListPane(Project project, AnalysisToolJComponentWindow delegate) {
        this.delegate = delegate;
        setLayout(new BorderLayout(5,5));

        JLabel label = new JLabel("Active Refactorings");
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
                int index = listPane.getSelectedIndex();
                if (index > -1) {
                    delegate.deselectOthers(this);
                    currentlyShownElement = model.getElementAt(index);
                    delegate.showDescription(new RefactoringDescription(currentlyShownElement));
                }
            }
        }));

        UIActiveRefactoringCollectionListener listener = new UIActiveRefactoringCollectionListener();
        project.getMessageBus().connect().subscribe(RefactoringEngineComponent.ACTIVE_REFACTORINGS_TOPIC, listener);
        RefactoringEngineComponent refactoringEngineComponent = project.getComponent(RefactoringEngineComponent.class);
        listener.accept(refactoringEngineComponent.activeRefactorings());


        Component scrollListPane = new JBScrollPane(listPane);
        add(scrollListPane, BorderLayout.CENTER);
    }

    public void clearSelection() {
        listPane.clearSelection();
        currentlyShownElement = null;
    }

    private class UIActiveRefactoringCollectionListener implements ActiveRefactoringCollectionListener {
        @Override
        public void accept(Collection<Refactoring> refactorings) {
            ApplicationManager.getApplication().invokeLater(() -> {
                model.batchUpdateContents(refactorings);
                invalidate();
                repaint();

                if (currentlyShownElement != null && !refactorings.contains(currentlyShownElement)) {
                    delegate.showDescription(null);
                }
            });

        }
    }
}
