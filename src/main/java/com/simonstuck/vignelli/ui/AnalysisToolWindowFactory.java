package com.simonstuck.vignelli.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class AnalysisToolWindowFactory implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(AnalysisToolWindowFactory.class.getName());

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        AnalysisToolJComponentWindow analysisComponentView = new AnalysisToolJComponentWindow(project);
        ContentManager manager = toolWindow.getContentManager();
        Content content = manager.getFactory().createContent(analysisComponentView,"",true);
        manager.addContent(content);
        LOG.debug("analysis window content created and added.");
    }

}
