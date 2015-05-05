package com.simonstuck.vignelli.psi.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorUtil {

    /**
     * Attempts to navigate the editor to the given element and requests focus.
     * @param element The element to navigate to.
     */
    public static void navigateToElement(PsiElement element) {
        navigateToElement(element, true);
    }

    /**
     * Attempts to navigate the editor to the given element
     * @param element The element to navigate to.
     * @param requestFocus Whether or not the editor should request focus.
     */
    public static void navigateToElement(PsiElement element, boolean requestFocus) {
        if (element == null) {
            return;
        }
        PsiElement navigationElement = element.getNavigationElement();
        if (navigationElement != null && navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate()) {
            ((Navigatable) navigationElement).navigate(requestFocus);
        }
    }

    /**
     * Focuses the user on the editor to enable direct typing without having to click on the editor.
     */
    public static void focusOnEditorForTyping(Editor editor) {
        IdeFocusManager ideFocusManager = IdeFocusManager.getInstance(editor.getProject());
        ideFocusManager.requestFocus(editor.getContentComponent(), true);
    }

    /**
     * Returns an editor instance for the given {@link com.intellij.psi.PsiElement}
     * <p>If the currently open text editor contains the element, it is returned.</p>
     * <p>If any of the other open editors contains it that editor is returned.</p>
     * <p>Otherwise, a new editor is created.</p>
     * @param element The element for which to find an editor
     * @return The editor for that element
     */
    @NotNull
    public static Editor getEditor(PsiElement element) {
        Project project = element.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = documentManager.getDocument(element.getContainingFile());
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        Editor currentTextEditor = fileEditorManager.getSelectedTextEditor();
        assert document != null;
        EditorFactory editorFactory = EditorFactory.getInstance();
        List<Editor> editors = new ArrayList<Editor>(Arrays.asList(editorFactory.getEditors(document)));
        if (currentTextEditor != null && editors.contains(currentTextEditor)) {
            return currentTextEditor;
        } else if (!editors.isEmpty()) {
            return editors.get(0);
        } else {
            return editorFactory.createEditor(document);
        }
    }


}
