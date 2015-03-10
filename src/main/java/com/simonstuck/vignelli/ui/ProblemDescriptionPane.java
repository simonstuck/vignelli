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
import javax.swing.text.html.HTMLEditorKit;

class ProblemDescriptionPane extends JEditorPane {
    private static final Logger LOG = Logger.getInstance(ProblemDescriptionPane.class.getName());
    public static final String VIGNELLI_SCHEME = "vignelli";
    private static Template DEFAULT_TEMPLATE;
    private static String STYLES;

    static {
        try {
            String strTemplate = IOUtils.readFile(ProblemDescriptionPane.class.getResource("/problemDescriptionTemplates/emptyDescription.html").toURI());
            DEFAULT_TEMPLATE = new HTMLFileTemplate(strTemplate);
            STYLES = IOUtils.readFile(ProblemDescriptionPane.class.getResource("/problemDescriptionTemplates/styles.css").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private ProblemIdentification shownIdentification;

    public ProblemDescriptionPane() {
        setLayout(new BorderLayout());
        setOpaque(false);

        HTMLEditorKit kit = new HTMLEditorKit();
        Document doc = kit.createDefaultDocument();
        kit.getStyleSheet().addRule(STYLES);
        setEditable(false);
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
        Optional<TrainWreckVariableImprovementOpportunity> opportunity = shownIdentification.improvementOpportunity();
        if (!opportunity.isPresent()) {
            LOG.warn("Tried to launch refactoring which does not exist. event=[" + event + "]");
        } else {
            opportunity.get().beginRefactoring();
        }
    }

    public void showDescription(@NotNull ProblemIdentification id) {
        shownIdentification = id;
        Map<String, Object> contentMap = new HashMap<>();
        Template template = new HTMLFileTemplate(id.descriptionTemplate());
        Optional<TrainWreckVariableImprovementOpportunity> opp = id.improvementOpportunity();
        if (opp.isPresent()) {
            contentMap.put("improvement", opp.get().toString());
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
