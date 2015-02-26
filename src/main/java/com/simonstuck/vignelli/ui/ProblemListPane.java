package com.simonstuck.vignelli.ui;

import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import javax.swing.JList;

class ProblemListPane extends JList<ProblemIdentification> {
    public ProblemListPane(ProblemIdentificationCollectionListModel dataModel) {
        super(dataModel);
    }
}
