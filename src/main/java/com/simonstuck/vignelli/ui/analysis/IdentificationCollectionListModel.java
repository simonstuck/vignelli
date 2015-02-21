package com.simonstuck.vignelli.ui.analysis;

import com.simonstuck.vignelli.inspection.identification.Identification;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdentificationCollectionListModel implements ListModel<Identification> {

    @NotNull
    private Set<ListDataListener> listeners = new HashSet<ListDataListener>();
    private List<Identification> problemIdentifications = new ArrayList<Identification>();

    public void add(Identification id) {
        problemIdentifications.add(id);
        int addedIndex = problemIdentifications.size() - 1;
        ListDataEvent addedEvent = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, addedIndex, addedIndex);
        for (ListDataListener l : listeners) {
            l.intervalAdded(addedEvent);
        }
    }

    public void addAll(Collection<Identification> ids) {
        int startIndex = problemIdentifications.size();
        problemIdentifications.addAll(startIndex,ids);
        ListDataEvent addedEvent = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, startIndex, startIndex + ids.size() - 1);
        for (ListDataListener l : listeners) {
            l.intervalAdded(addedEvent);
        }
    }

    @Override
    public int getSize() {
        return problemIdentifications.size();
    }

    @Override
    public Identification getElementAt(int index) {
        return problemIdentifications.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public boolean contains(Identification id) {
        return problemIdentifications.contains(id);
    }
}
