package com.simonstuck.vignelli.ui.analysis;

import com.simonstuck.vignelli.inspection.identification.Identification;

import javax.swing.*;

public class ProblemListPane extends JList<Identification> {
    public ProblemListPane(ListModel<Identification> dataModel) {
        super(dataModel);
    }
}
