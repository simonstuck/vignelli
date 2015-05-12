package com.simonstuck.vignelli.evaluation.ui;

import javax.swing.DefaultListSelectionModel;

/**
 * List selection model implementation that allows toggling of rows by clicking repeatedly.
 * Implementation taken from Taken from http://stackoverflow.com/a/9196431
 */
class ToggleListSelectionModel extends DefaultListSelectionModel {
    private static final long serialVersionUID = 1L;

    boolean gestureStarted = false;

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if(!gestureStarted){
            if (isSelectedIndex(index0)) {
                super.removeSelectionInterval(index0, index1);
            } else {
                super.addSelectionInterval(index0, index1);
            }
        }
        gestureStarted = true;
    }

    @Override
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (isAdjusting == false) {
            gestureStarted = false;
        }
    }

}
