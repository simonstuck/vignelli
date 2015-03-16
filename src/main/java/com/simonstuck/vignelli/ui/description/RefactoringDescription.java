package com.simonstuck.vignelli.ui.description;

import com.simonstuck.vignelli.refactoring.Refactoring;

import javax.swing.event.HyperlinkEvent;

public class RefactoringDescription implements Description {

    private final Refactoring refactoring;

    public RefactoringDescription(Refactoring refactoring) {
        this.refactoring = refactoring;
    }

    @Override
    public String render() {
        return "Refactoring Description";
    }

    @Override
    public void handleVignelliLinkEvent(HyperlinkEvent event) {

    }
}
