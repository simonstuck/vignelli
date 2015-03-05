package com.simonstuck.vignelli.ui;

import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import javax.swing.JList;
import javax.swing.ListModel;

class ProblemListPane extends JList<ProblemIdentification> {
    public ProblemListPane(ListModel<ProblemIdentification> dataModel) {
        super(dataModel);
    }
}
