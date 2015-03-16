package com.simonstuck.vignelli.ui.description;

import com.intellij.openapi.diagnostic.Logger;
import com.simonstuck.vignelli.inspection.TrainWreckVariableImprovementOpportunity;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.event.HyperlinkEvent;

public class ProblemIdentificationDescription implements Description {

    private static final Logger LOG = Logger.getInstance(ProblemIdentificationDescription.class.getName());

    @NotNull
    private final ProblemIdentification id;

    public ProblemIdentificationDescription(@NotNull ProblemIdentification id) {
        this.id = id;
    }

    @Override
    public String render() {
        Map<String, Object> contentMap = new HashMap<>();
        Template template = new HTMLFileTemplate(id.descriptionTemplate());
        Optional<TrainWreckVariableImprovementOpportunity> opp = id.improvementOpportunity();
        if (opp.isPresent()) {
            contentMap.put("improvement", opp.get().toString());
        }
        return template.render(contentMap);
    }

    @Override
    public void handleVignelliLinkEvent(HyperlinkEvent event) {
        LOG.info("vignelli event: " + event);
        Optional<TrainWreckVariableImprovementOpportunity> opportunity = id.improvementOpportunity();
        if (!opportunity.isPresent()) {
            LOG.warn("Tried to launch refactoring which does not exist. event=[" + event + "]");
        } else {
            opportunity.get().beginRefactoring();
        }
    }
}
