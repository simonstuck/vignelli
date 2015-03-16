package com.simonstuck.vignelli.ui.description;

import javax.swing.event.HyperlinkEvent;

public interface Description {
    String render();

    void handleVignelliLinkEvent(HyperlinkEvent event);
}
