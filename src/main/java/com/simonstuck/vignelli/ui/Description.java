package com.simonstuck.vignelli.ui;

import javax.swing.event.HyperlinkEvent;

interface Description {
    String render();

    void handleVignelliLinkEvent(HyperlinkEvent event);
}
