package com.simonstuck.vignelli.ui;

import com.intellij.openapi.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.DefaultListModel;

public class BatchUpdateListModel<T> extends DefaultListModel<T> {

    /**
     * Replaces the contents of the data model with the new given contents.
     * <p>If some of the given elements already exist, they remain intact and are not removed and re-added.</p>
     * @param newContents The new contents
     */
    public void batchUpdateContents(Collection<T> newContents) {
        List<Integer> indexesToRemove = getIndexesToRemove(newContents);
        Collection<Pair<Integer,Integer>> rangesToRemove = getRangesToRemove(indexesToRemove);

        for (Pair<Integer,Integer> range : rangesToRemove) {
            removeRange(range.getFirst(),range.getFirst() + range.getSecond() - 1);
        }

        newContents.stream().filter(newProblem -> !contains(newProblem)).forEach(this::addElement);
    }

    /**
     * Compute the indexes that have to be removed based on the new contents.
     * @param newContents The new contents
     * @return A list of indexes to be removed
     */
    private List<Integer> getIndexesToRemove(Collection<T> newContents) {
        List<Integer> indexesToRemove = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            if (!newContents.contains(getElementAt(i))) {
                indexesToRemove.add(i);
            }
        }
        return indexesToRemove;
    }

    /**
     * Compute the ranges of elements that need to be removed from an ordered list of indexes.
     * @param indexesToRemove The indexes to be removed
     * @return The collection of ranges to be removed
     */
    private Collection<Pair<Integer,Integer>> getRangesToRemove(List<Integer> indexesToRemove) {
        Collection<Pair<Integer, Integer>> rangesToRemove = new HashSet<>();
        int startIndex = Integer.MIN_VALUE;
        int rangeLength = 0;
        for (int index : indexesToRemove) {
            if (rangeLength == 0) {
                // first time around
                startIndex = index;
            } else if (index > startIndex + rangeLength) {
                // we have skipped at least one number and need to add the old one to the range
                if (rangeLength > 0) {
                    rangesToRemove.add(new Pair<>(startIndex, rangeLength));
                }
                rangeLength = 0;
                startIndex = index;
            }
            rangeLength++;
        }
        if (!indexesToRemove.isEmpty()) {
            // there is one last range to add
            rangesToRemove.add(new Pair<>(startIndex, rangeLength));
        }
        return rangesToRemove;
    }
}
