package com.simonstuck.vignelli.ui;

import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.ui.description.HTMLFileTemplate;
import com.simonstuck.vignelli.ui.description.Template;
import com.simonstuck.vignelli.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

class ProblemDescriptionPane extends JEditorPane {
    private static Template DEFAULT_TEMPLATE;

    static {
        try {
            String strTemplate = IOUtils.readFile(ProblemDescriptionPane.class.getResource("/problemDescriptionTemplates/emptyDescription.html").toURI());
            DEFAULT_TEMPLATE = new HTMLFileTemplate(strTemplate);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public ProblemDescriptionPane() {
        setLayout(new BorderLayout());
        setEditable(false);
        setOpaque(false);

        EditorKit kit = new HTMLEditorKit();
        Document doc = kit.createDefaultDocument();
        setEditorKit(kit);
        setDocument(doc);

        addListeners();

        showDefault();
    }

    private void addListeners() {
        addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(event.getURL().toURI());
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void showDescription(@NotNull ProblemIdentification id) {
        Map<String, String> contentMap = new HashMap<>();
        Template template = new HTMLFileTemplate(id.descriptionTemplate());
        setText(template.render(contentMap));
        this.updateUI();
    }

    public void showDefault() {
        Map<String, String> values = new HashMap<>();
        values.put("AWESOME", "GLIDING");
        setText(DEFAULT_TEMPLATE.render(values));
        updateUI();
    }
}
