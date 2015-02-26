package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProblemIdentificationCache implements ProjectComponent {
    /**
     * When subscribed to this topic, clients will receive regular updates about the problems in the currently selected file.
     */
    public static final Topic<ProblemIdentificationCollectionListener> INSPECTION_IDENTIFICATION_TOPIC
            = Topic.create("Design Problem",ProblemIdentificationCollectionListener.class);

    private static final String COMPONENT_NAME = "Vignelli Problem Identification Cache";

    private final Map<VirtualFile, IdentificationCollection<ProblemIdentification>> problemIdentifications;
    private final MessageBus messageBus;

    private VirtualFile selectedFile;


    /**
     * Creates a new {@link com.simonstuck.vignelli.inspection.ProblemIdentificationCache}.
     * @param project The project for which the cache is created
     */
    public ProblemIdentificationCache(Project project) {
        problemIdentifications = new HashMap<VirtualFile, IdentificationCollection<ProblemIdentification>>();
        messageBus = project.getMessageBus();
        messageBus.connect(project).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new ProblemFileSelectionListener());
    }

    /**
     * Updates the problems for the given file.
     * <p>This replaces all problems for the file, so this method cannot be called with incremental changes.</p>
     * @param file The file for which problems are updated
     * @param problems The new problems that were found
     */
    public void updateFileProblems(VirtualFile file, IdentificationCollection<ProblemIdentification> problems) {
        problemIdentifications.put(file, problems);
        broadcastCurrentProblems();
    }

    /**
     * Broadcast the problems for the current file to all subscribers to the INSPECTION_IDENTIFICATION_TOPIC.
     */
    private void broadcastCurrentProblems() {
        messageBus.syncPublisher(INSPECTION_IDENTIFICATION_TOPIC).accept(selectedFileProblems());
    }

    /**
     * Gets the problems for the currently selected file in the editor.
     * @return The problems for the file that is currently selected
     */
    public IdentificationCollection<ProblemIdentification> selectedFileProblems() {
        return problemIdentifications.get(selectedFile);
    }

    @Override
    public void projectOpened() {}

    @Override
    public void projectClosed() {}

    @Override
    public void initComponent() {}

    @Override
    public void disposeComponent() {}

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    private class ProblemFileSelectionListener implements FileEditorManagerListener {
        @Override
        public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            problemIdentifications.put(file, new IdentificationCollection<ProblemIdentification>());
        }

        @Override
        public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            problemIdentifications.remove(file);
        }

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            selectedFile = event.getNewFile();
            broadcastCurrentProblems();
        }
    }
}
