package com.simonstuck.vignelli.ui.description;

import java.util.Observable;
import javax.swing.event.HyperlinkEvent;

public abstract class Description extends Observable {
    public abstract String render();

    public abstract void handleVignelliLinkEvent(HyperlinkEvent event);
}
