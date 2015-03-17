package com.simonstuck.vignelli.refactoring;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RefactoringEngineComponent implements ProjectComponent, RefactoringTracker {

    /**
     * When subscribed to this topic, clients, will receive updates about the active refactorings.
     */
    public static final Topic<ActiveRefactoringCollectionListener> ACTIVE_REFACTORINGS_TOPIC
            = Topic.create("Active Refactorings", ActiveRefactoringCollectionListener.class);

    private final Project project;

    private Set<Refactoring> activeRefactorings = new HashSet<>();

    public RefactoringEngineComponent(Project project) {
        this.project = project;
    }

    /**
     * Adds the refactoring to the active ones and notifies subscribers about the change.
     * @param refactoring The refactoring to add
     */
    @Override
    public void add(Refactoring refactoring) {
        if (!activeRefactorings.contains(refactoring)) {
            activeRefactorings.add(refactoring);
            broadcastActiveRefactorings();
        }
    }

    /**
     * Removes the refactoring from the active ones and notifies subscribers about the change.
     * @param refactoring The refactoring to remove
     */
    @Override
    public void remove(Refactoring refactoring) {
        activeRefactorings.remove(refactoring);
        broadcastActiveRefactorings();
    }

    @Override
    public Collection<Refactoring> activeRefactorings() {
        return new HashSet<>(activeRefactorings);
    }

    /**
     * Broadcasts all active refactorings to all subscribers.
     */
    protected void broadcastActiveRefactorings() {
        project.getMessageBus().syncPublisher(ACTIVE_REFACTORINGS_TOPIC).accept(new HashSet<>(activeRefactorings));
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
        return "Vignelli Refactoring Engine";
    }
}
