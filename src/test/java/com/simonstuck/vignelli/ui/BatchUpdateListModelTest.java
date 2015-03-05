package com.simonstuck.vignelli.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class BatchUpdateListModelTest {


    private BatchUpdateListModel<String> model;
    private MyListDataListener myListDataListener;

    @Before
    public void setUp() throws Exception {
        model = new BatchUpdateListModel<>();
        model.addElement("Hello");
        model.addElement("World");
        model.addElement("Imperial");
        model.addElement("Project");
        myListDataListener = new MyListDataListener();
        model.addListDataListener(myListDataListener);
    }

    @Test
    public void shouldRemoveNoElementsIfAllElementsAreInNewContent() throws Exception {
        Collection<String> newContents = Arrays.asList("Hello", "Imperial", "Project");
        model.batchUpdateContents(newContents);


        assertEquals(1, myListDataListener.intervalsRemoved.size());
        assertEquals(1, myListDataListener.intervalsRemoved.get(0).getIndex0());
        assertEquals(1, myListDataListener.intervalsRemoved.get(0).getIndex1());

        assertTrue(myListDataListener.intervalsAdded.isEmpty());
        assertTrue(myListDataListener.contentsChanged.isEmpty());
    }

    private static class MyListDataListener implements ListDataListener {
        List<ListDataEvent> intervalsAdded = new ArrayList<>();
        List<ListDataEvent> intervalsRemoved = new ArrayList<>();
        List<ListDataEvent> contentsChanged = new ArrayList<>();


        @Override
        public void intervalAdded(ListDataEvent e) {
            intervalsAdded.add(e);
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            intervalsRemoved.add(e);
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            contentsChanged.add(e);
        }
    }
}