package com.simonstuck.vignelli.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.simonstuck.vignelli.ui.description.Description;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

class DescriptionPane extends JEditorPane implements Observer {
    private static final Logger LOG = Logger.getInstance(DescriptionPane.class.getName());
    public static final String VIGNELLI_SCHEME = "vignelli";
    private static Description DEFAULT_DESCRIPTION = new DefaultDescription();

    private Description description;

    public DescriptionPane() {
        setLayout(new BorderLayout());
        setOpaque(false);

        HTMLEditorKit kit = new HTMLEditorKit();
        Document doc = kit.createDefaultDocument();

        try {
            String styles = IOUtils.readFile(DescriptionPane.class.getResource("/descriptionTemplates/styles.css").toURI());
            kit.getStyleSheet().addRule(styles);
        } catch (IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }


        setEditable(false);
        setEditorKit(kit);
        setDocument(doc);

        addListeners();

        showDefault();
    }

    /**
     * Adds event listeners to editor pane.
     * For hyperlinks, the current description's handler is called.
     */
    private void addListeners() {
        addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    if (event.getDescription().startsWith(VIGNELLI_SCHEME)) {
                        description.handleVignelliLinkEvent(event);
                    } else {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    }
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void showDescription(@NotNull Description description) {

        // Unsubscribe from old observer
        if (this.description != null) {
            this.description.deleteObserver(this);
        }

        this.description = description;
        this.description.addObserver(this);
        render();
    }

    /**
     * Renders the current description.
     */
    private void render() {
        setText(description.render());
        validate();
        repaint();
    }

    public void showDefault() {
        showDescription(DEFAULT_DESCRIPTION);
    }

    @Override
    public void update(Observable o, Object arg) {
        // Updated
        render();
    }

    private static class DefaultDescription extends Description {
        @Override
        public String render() {
            String strTemplate = null;
            try {
                strTemplate = IOUtils.readFile(DescriptionPane.class.getResource("/descriptionTemplates/emptyDescription.html").toURI());
            } catch (IOException | URISyntaxException e) {
                LOG.error(e.getMessage(), e);
            }
            Template defaultTemplate = new HTMLFileTemplate(strTemplate);
            return defaultTemplate.render(new HashMap<>());
        }

        @Override
        public void handleVignelliLinkEvent(HyperlinkEvent event) {}
    }
}
