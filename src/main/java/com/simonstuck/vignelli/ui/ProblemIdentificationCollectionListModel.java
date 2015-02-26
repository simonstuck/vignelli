package com.simonstuck.vignelli.ui;

import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

class ProblemIdentificationCollectionListModel implements ListModel<ProblemIdentification> {

    @NotNull
    private final Set<ListDataListener> listeners = new HashSet<>();
    private final List<ProblemIdentification> problemIdentifications = new ArrayList<>();

    @Override
    public int getSize() {
        return problemIdentifications.size();
    }

    @Override
    public ProblemIdentification getElementAt(int index) {
        return problemIdentifications.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(listener);
    }

    /**
     * Replaces the contents of the data model with the given identifications.
     * <p>If some of the given identifications already exist, they remain intact and are not replaced.</p>
     * @param identifications The identifications to replace the previous contents with
     */
    public void replaceWithNewContents(Collection<ProblemIdentification> identifications) {
        problemIdentifications.stream().filter(existingProblem -> !identifications.contains(existingProblem)).forEach(this::remove);
        identifications.stream().filter(newProblem -> !problemIdentifications.contains(newProblem)).forEach(this::add);
    }

    /**
     * Adds a problem identification to the data model.
     * @param id The identification to add
     */
    private void add(ProblemIdentification id) {
        problemIdentifications.add(id);
        int addedIndex = problemIdentifications.size() - 1;
        ListDataEvent addedEvent = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, addedIndex, addedIndex);
        for (ListDataListener l : listeners) {
            l.intervalAdded(addedEvent);
        }
    }

    /**
     * Removes a problem identification from the data model.
     * @param id The identification to remove
     */
    private void remove(ProblemIdentification id) {
        int removalIndex = problemIdentifications.indexOf(id);
        problemIdentifications.remove(id);
        ListDataEvent removedEvent = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, removalIndex, removalIndex);
        for (ListDataListener l : listeners) {
            l.intervalRemoved(removedEvent);
        }
    }
}
