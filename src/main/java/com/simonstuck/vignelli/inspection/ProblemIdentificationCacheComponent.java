package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ProblemIdentificationCacheComponent implements ProjectComponent {
    /**
     * When subscribed to this topic, clients will receive regular updates about the problems in the currently selected file.
     */
    public static final Topic<ProblemIdentificationCollectionListener> INSPECTION_IDENTIFICATION_TOPIC
            = Topic.create("Design Problem",ProblemIdentificationCollectionListener.class);

    private static final String COMPONENT_NAME = "Vignelli Problem Identification Cache";

    private final Map<VirtualFile, Collection<ProblemIdentification>> problemIdentifications;
    private final ProblemFileSelectionListener problemFileSelectionListener;
    private final Project project;

    protected VirtualFile selectedFile;


    /**
     * Creates a new {@link ProblemIdentificationCacheComponent}.
     * @param project The project for which the cache is created
     */
    public ProblemIdentificationCacheComponent(Project project) {
        this.project = project;
        problemIdentifications = new HashMap<>();
        problemFileSelectionListener = new ProblemFileSelectionListener();
        subscribeToFileSelectionChanges(project);
    }

    protected void subscribeToFileSelectionChanges(Project project) {
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect(project).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, problemFileSelectionListener);
    }

    /**
     * Updates the problems for the given file.
     * <p>This replaces all problems for the file, so this method cannot be called with incremental changes.</p>
     * @param file The file for which problems are updated
     * @param problems The new problems that were found
     */
    public void updateFileProblems(VirtualFile file, Collection<ProblemIdentification> problems) {
        problemIdentifications.put(file, problems);
        if (file.equals(selectedFile)) {
            broadcastCurrentProblems(problems);
        }
    }

    /**
     * Broadcast the problems for the current file to all subscribers to the INSPECTION_IDENTIFICATION_TOPIC.
     */
    protected void broadcastCurrentProblems(Collection<ProblemIdentification> problems) {
        project.getMessageBus().syncPublisher(INSPECTION_IDENTIFICATION_TOPIC).accept(problems);
    }

    /**
     * Gets the problems for the currently selected file in the editor.
     * @return The problems for the file that is currently selected
     */
    public Collection<ProblemIdentification> selectedFileProblems() {
        Collection<ProblemIdentification> result = problemIdentifications.get(selectedFile);
        if (result != null) {
            return result;
        } else {
            return new ArrayList<>();
        }
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
            problemIdentifications.put(file, new LinkedList<>());
        }

        @Override
        public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            problemIdentifications.remove(file);
        }

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            selectedFile = event.getNewFile();
            broadcastCurrentProblems(selectedFileProblems());
        }
    }
}
