package com.simonstuck.vignelli.ui.description;

import com.google.common.base.Optional;
import com.intellij.openapi.diagnostic.Logger;
import com.simonstuck.vignelli.inspection.ImprovementOpportunity;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import javax.swing.event.HyperlinkEvent;

public class ProblemIdentificationDescription extends Description {

    private static final Logger LOG = Logger.getInstance(ProblemIdentificationDescription.class.getName());

    @NotNull
    private final ProblemIdentification id;

    public ProblemIdentificationDescription(@NotNull ProblemIdentification id) {
        this.id = id;
    }

    @Override
    public String render() {
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        Template template = new HTMLFileTemplate(id.template());
        Optional<? extends ImprovementOpportunity> opp = id.improvementOpportunity();
        if (opp.isPresent()) {
            contentMap.put("improvement", opp.get().toString());
        }
        return template.render(contentMap);
    }

    @Override
    public void handleVignelliLinkEvent(HyperlinkEvent event) {
        LOG.info("vignelli event: " + event);
        Optional<? extends ImprovementOpportunity> opportunity = id.improvementOpportunity();
        if (!opportunity.isPresent()) {
            LOG.warn("Tried to launch refactoring which does not exist. event=[" + event + "]");
        } else {
            opportunity.get().beginRefactoring();
        }
    }
}
