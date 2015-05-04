package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import com.intellij.util.messages.Topic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VignelliRefactoringListener implements RefactoringElementListenerProvider {

    public static final Topic<RenameListener> RENAME_LISTENER_TOPIC = Topic.create("element renamed", RenameListener.class);

    @Nullable
    @Override
    public synchronized RefactoringElementListener getListener(PsiElement element) {
        return new RefactoringElementListener() {

            @Override
            public void elementMoved(@NotNull PsiElement newElement) {}

            @Override
            public void elementRenamed(@NotNull PsiElement newElement) {
                Project project = newElement.getProject();
                project.getMessageBus().syncPublisher(RENAME_LISTENER_TOPIC).consume(newElement);
            }
        };
    }
}
