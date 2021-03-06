package com.simonstuck.vignelli.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBScrollPane;
import com.simonstuck.vignelli.ui.description.Description;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

class DescriptionPane extends JEditorPane implements Observer, InformationPane {
    private static final Logger LOG = Logger.getInstance(DescriptionPane.class.getName());
    public static final String VIGNELLI_SCHEME = "vignelli";
    private static Description DEFAULT_DESCRIPTION = new DefaultDescription();

    private Description description;

    @Nullable
    private Delegate delegate;

    /**
     * Creates a new {@link com.simonstuck.vignelli.ui.DescriptionPane} without a delegate
     */
    public DescriptionPane() {
        this(null);
    }

    /**
     * Creates a new {@link com.simonstuck.vignelli.ui.DescriptionPane} with the given {@link com.simonstuck.vignelli.ui.DescriptionPane.Delegate}
     * @param delegate The delegate to notify
     */
    public DescriptionPane(@Nullable Delegate delegate) {
        this.delegate = delegate;
        HTMLEditorKit kit = new HTMLEditorKit();
        Document doc = kit.createDefaultDocument();

        try {
            String styles = IOUtil.readFile("descriptionTemplates/styles.css");
            kit.getStyleSheet().addRule(styles);
        } catch (IOException e) {
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
        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        if (event.getDescription().startsWith(VIGNELLI_SCHEME)) {
                            description.handleVignelliLinkEvent(event);
                        } else {
                            Desktop.getDesktop().browse(event.getURL().toURI());
                        }
                    } catch (IOException e1) {
                        LOG.info(e1);
                    } catch (URISyntaxException e1) {
                        LOG.info(e1);
                    }
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
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                render();
                if (delegate != null) {
                    delegate.didUpdateDescriptionPane(DescriptionPane.this);
                }
            }
        });
    }

    private static class DefaultDescription extends Description {

        private static final String DEFAULT_DESCRIPTION_PATH = "descriptionTemplates/emptyDescription.html";

        @Override
        public String render() {
            String strTemplate = null;
            try {
                strTemplate = IOUtil.readFile(DEFAULT_DESCRIPTION_PATH);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            Template defaultTemplate = new HTMLFileTemplate(strTemplate);
            return defaultTemplate.render(new HashMap<String, Object>());
        }

        @Override
        public void handleVignelliLinkEvent(HyperlinkEvent event) {}
    }

    /**
     * Delegates of the description pane can get notified when the description pane is updated.
     */
    public interface Delegate {
        /**
         * Notifies delegates that the description pane has been updated.
         * @param descriptionPane The description pane that was updated.
         */
        public void didUpdateDescriptionPane(DescriptionPane descriptionPane);
    }

}
