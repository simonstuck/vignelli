package com.simonstuck.vignelli.ui;

import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

class ProblemDescriptionPane extends JEditorPane {
    public ProblemDescriptionPane() {
        HTMLEditorKit kit = new HTMLEditorKit();
        setLayout(new BorderLayout());
        setEditorKit(kit);

        setEditable(false);
        setOpaque(false);
        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    System.out.println("Link clicked!");
                    try {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {background:red;}");

        String htmlString = "<html>\n"
                + "<body>\n"
                + "<h1>Welcome!</h1>\n"
                + "<h2>This is an H2 header</h2>\n"
                + "<p>This is some sample text</p>\n"
                + "<p><a href=\"http://devdaily.com/blog/\">devdaily blog</a></p>\n"
                + "</body></html>\n";

        Document doc = kit.createDefaultDocument();
        setDocument(doc);
        setText(htmlString);
    }

    public void showDescription(ProblemIdentification id) {
        String htmlString = "<html>\n"
                + "<body>\n"
                + "<h1>" + id.name() + "</h1>\n"
                + "<p>" + id.shortDescription() + "</p>\n"
                + "<p>" + id.longDescription() + "</p>\n"
                + "</body></html>\n";

        setText(htmlString);
        this.updateUI();
    }
}
