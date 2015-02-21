package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProblemIdentificationCache implements ProjectComponent {

    private final Project project;
    private IdentificationCollection<ProblemIdentification> problemIdentifications;

    public ProblemIdentificationCache(Project project) {
        this.project = project;
        problemIdentifications = new IdentificationCollection<ProblemIdentification>();
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Vignelli Problem Identification Cache";
    }

    public void updateProblems(Collection<ProblemDescriptor> problems) {
        final Set<ProblemDescriptor> newProblems = new HashSet<ProblemDescriptor>(problems);
        final IdentificationCollection<ProblemIdentification> result = new IdentificationCollection<ProblemIdentification>();

        for (ProblemDescriptor problemDescriptor : newProblems) {
            ProblemIdentification id = ProblemIdentification.createWithProblemDescriptor(problemDescriptor);
            result.add(id);
        }
        problemIdentifications.addAll(result);

        project.getMessageBus().syncPublisher(VignelliLocalInspectionTool.INSPECTION_IDENTIFICATION_TOPIC).accept(result);
    }

    public IdentificationCollection<ProblemIdentification> getAllProblems() {
        return problemIdentifications;
    }
}
