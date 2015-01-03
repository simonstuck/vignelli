package com.simonstuck.vignelli;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class VignelliProjectComponent implements ProjectComponent {

    private final Project project;

    public VignelliProjectComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "VignelliProjectComponent";
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }
}
