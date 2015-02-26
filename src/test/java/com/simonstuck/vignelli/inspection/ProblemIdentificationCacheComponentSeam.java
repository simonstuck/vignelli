package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

public class ProblemIdentificationCacheComponentSeam extends ProblemIdentificationCacheComponent {

    IdentificationCollection<ProblemIdentification> lastBroadcast;

    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.ProblemIdentificationCacheComponent}.
     *
     * @param project The project for which the cache is created
     */
    public ProblemIdentificationCacheComponentSeam(Project project) {
        super(project);
    }

    @Override
    protected void subscribeToFileSelectionChanges(Project project) {
    }

    @Override
    protected void broadcastCurrentProblems(IdentificationCollection<ProblemIdentification> problems) {
        lastBroadcast = problems;
    }

    public void selectFile(VirtualFile file) {
        selectedFile = file;
    }
}
