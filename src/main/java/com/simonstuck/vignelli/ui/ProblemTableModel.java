package com.simonstuck.vignelli.ui;

import com.intellij.openapi.util.Pair;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ProblemTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = { "#", "Problem", "Code" };

    private final List<ProblemIdentification> problemData = new ArrayList<>();

    @Override
    public int getRowCount() {
        return problemData.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public ProblemIdentification getProblemDataAt(int row) {
        return problemData.get(row);
    }

    @Override
    public Object getValueAt(int row, int column) {
        ProblemIdentification problem = getProblemDataAt(row);
        switch (column) {
            case 0:
                return problem.getProblemDescriptor().getLineNumber();
            case 1:
                return problem.toString();
            case 2:
                return problem.getProblemDescriptor().getPsiElement().getText();
            default:
                return "";
        }
    }

    public void batchUpdateContents(List<ProblemIdentification> newProblems) {
        List<Integer> indexesToRemove = getIndexesToRemove(newProblems);
        Collection<Pair<Integer,Integer>> rangesToRemove = getRangesToRemove(indexesToRemove);
        int removed = 0;

        for (Pair<Integer,Integer> range : rangesToRemove) {

            for (int i = range.getFirst(); i < range.getSecond(); i++) {
                problemData.remove(i - removed);
                removed++;
            }

            fireTableRowsDeleted(range.getFirst(), range.getSecond() - 1);
        }

        for (int i = 0; i < newProblems.size(); i++) {
            ProblemIdentification item = newProblems.get(i);
            if (!problemData.contains(item)) {
                problemData.add(i, item);
                fireTableRowsInserted(i, i);
            }
        }
    }
    /**
     * Compute the indexes that have to be removed based on the new contents.
     * @param newContents The new contents
     * @return A list of indexes to be removed
     */
    private List<Integer> getIndexesToRemove(Collection<ProblemIdentification> newContents) {
        List<Integer> indexesToRemove = new ArrayList<>();

        for (int i = 0; i < getRowCount(); i++) {
            if (!newContents.contains(problemData.get(i))) {
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

    public boolean isEmpty() {
        return problemData.isEmpty();
    }

    public boolean contains(ProblemIdentification identification) {
        return problemData.contains(identification);
    }
}
