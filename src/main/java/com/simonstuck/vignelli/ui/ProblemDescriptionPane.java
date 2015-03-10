package com.simonstuck.vignelli.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.simonstuck.vignelli.inspection.TrainWreckVariableImprovementOpportunity;
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
import java.util.Optional;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

class ProblemDescriptionPane extends JEditorPane {
    private static final Logger LOG = Logger.getInstance(ProblemDescriptionPane.class.getName());
    public static final String VIGNELLI_SCHEME = "vignelli";
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
                    if (event.getDescription().startsWith(VIGNELLI_SCHEME)) {
                        handleVignelliLinkEvent(event);
                    } else {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    }
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void handleVignelliLinkEvent(HyperlinkEvent event) {
        LOG.info("vignelli event: " + event);
    }

    public void showDescription(@NotNull ProblemIdentification id) {
        Map<String, String> contentMap = new HashMap<>();
        Template template = new HTMLFileTemplate(id.descriptionTemplate());
        Optional<TrainWreckVariableImprovementOpportunity> opp = id.improvementOpportunity();
        if (opp.isPresent()) {
            contentMap.put("IMPROVEMENT", opp.get().toString());
        }
        setText(template.render(contentMap));
        validate();
        repaint();
    }

    public void showDefault() {
        setText(DEFAULT_TEMPLATE.render(new HashMap<>()));
        validate();
        repaint();
    }
}
