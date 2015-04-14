package com.simonstuck.vignelli.ui;

import com.intellij.psi.PsiElement;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ProblemTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = { "#", "Problem", "Code" };

    private final List<ProblemIdentification> problemData = new ArrayList<ProblemIdentification>();

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
                PsiElement psiElement = problem.getProblemDescriptor().getPsiElement();
                return psiElement != null ? psiElement.getText() : "";
            default:
                return "";
        }
    }

    public void batchUpdateContents(List<ProblemIdentification> newProblems) {
        int oldLength = problemData.size();
        problemData.clear();
        problemData.addAll(newProblems);

        fireTableRowsUpdated(0, newProblems.size() - 1);
        fireTableRowsDeleted(newProblems.size(), oldLength - 1);
    }
    public boolean isEmpty() {
        return problemData.isEmpty();
    }

    public boolean contains(ProblemIdentification identification) {
        return problemData.contains(identification);
    }
}
