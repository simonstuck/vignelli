package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ProblemIdentificationCacheComponent implements ProjectComponent {
    /**
     * When subscribed to this topic, clients will receive regular updates about the problems in the currently selected file.
     */
    public static final Topic<ProblemIdentificationCollectionListener> INSPECTION_IDENTIFICATION_TOPIC
            = Topic.create("Design Problem",ProblemIdentificationCollectionListener.class);

    private static final String COMPONENT_NAME = "Vignelli Problem Identification Cache";

    private final Map<VirtualFile, Map<Object, LinkedList<ProblemIdentification>>> problemIdentifications;

    private final ProblemFileSelectionListener problemFileSelectionListener;
    private final Project project;

    protected VirtualFile selectedFile;


    /**
     * Creates a new {@link ProblemIdentificationCacheComponent}.
     * @param project The project for which the cache is created
     */
    public ProblemIdentificationCacheComponent(Project project) {
        this.project = project;
        problemIdentifications = new HashMap<VirtualFile, Map<Object,LinkedList<ProblemIdentification>>>();
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
     * @param problemOwner The owner of the problems to be inserted, other owners may not remove problems of another owner
     * @param problems The new problems that were found
     */
    public synchronized void updateFileProblems(VirtualFile file, Object problemOwner, Collection<ProblemIdentification> problems) {
        Map<Object, LinkedList<ProblemIdentification>> problemOwnerIdentifications = problemIdentifications.get(file);
        if (problemOwnerIdentifications == null) {
            problemOwnerIdentifications = new HashMap<Object, LinkedList<ProblemIdentification>>();
        }
        problemOwnerIdentifications.put(problemOwner, new LinkedList<ProblemIdentification>(problems));
        problemIdentifications.put(file, problemOwnerIdentifications);
        if (file.equals(selectedFile)) {
            broadcastCurrentProblems(getFileProblems(selectedFile));
        }
    }

    /**
     * Broadcast the problems for the current file to all subscribers to the INSPECTION_IDENTIFICATION_TOPIC.
     */
    protected void broadcastCurrentProblems(Collection<ProblemIdentification> problems) {
        project.getMessageBus().syncPublisher(INSPECTION_IDENTIFICATION_TOPIC).consume(problems);
    }

    /**
     * Gets the problems for the currently selected file in the editor.
     * @return The problems for the file that is currently selected
     */
    public Collection<ProblemIdentification> selectedFileProblems() {
        return getFileProblems(selectedFile);
    }

    private Collection<ProblemIdentification> getFileProblems(VirtualFile file) {
        Map<Object, LinkedList<ProblemIdentification>> ownerResults = problemIdentifications.get(file);
        Collection<ProblemIdentification> result = new ArrayList<ProblemIdentification>();
        if (ownerResults != null) {
            for (LinkedList<ProblemIdentification> results : ownerResults.values()) {
                result.addAll(results);
            }
        }
        return result;
    }

    private synchronized void prepareElementForRemoval(PsiElement elementToBeRemoved) {
        PsiFile containingFile = elementToBeRemoved.getContainingFile();
        if (containingFile != null) {
            VirtualFile elementVirtualFile = containingFile.getVirtualFile();
            Map<Object, LinkedList<ProblemIdentification>> problemOwnerIdentifications = problemIdentifications.get(elementVirtualFile);
            if (problemOwnerIdentifications != null) {
                // there are elements for this file
                for (Map.Entry<Object, LinkedList<ProblemIdentification>> entry : problemOwnerIdentifications.entrySet()) {
                    Object owner = entry.getKey();
                    Set<ProblemIdentification> newProblemsForOwner = new HashSet<ProblemIdentification>();

                    for (ProblemIdentification problemIdentification : entry.getValue()) {
                        if (!PsiTreeUtil.isAncestor(elementToBeRemoved, problemIdentification.getElement(), false)) {
                            newProblemsForOwner.add(problemIdentification);
                        }
                    }
                    if (!newProblemsForOwner.containsAll(entry.getValue())) {
                        updateFileProblems(elementVirtualFile, owner, newProblemsForOwner);
                    }
                }
            }
        }
    }

    @Override
    public void projectOpened() {
        PsiManager psiManager = PsiManager.getInstance(project);
        psiManager.addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {
                super.beforeChildRemoval(event);
                prepareElementForRemoval(event.getChild());
            }

            @Override
            public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {
                super.beforeChildReplacement(event);
                prepareElementForRemoval(event.getOldChild());
            }
        }, project);
    }

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
            problemIdentifications.put(file, new HashMap<Object, LinkedList<ProblemIdentification>>());
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
