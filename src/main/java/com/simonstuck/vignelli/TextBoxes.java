package main.java.com.simonstuck.vignelli;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class TextBoxes extends AnAction {

    public TextBoxes() {
        super("Text_Boxes");
    }

    /**
     * Shows message dialog greeting the user.
     * @param event Event corresponding to the action
     */
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String title = "What is your name?";
        String query = "Input your name";
        String txt = Messages.showInputDialog(project, title, query, Messages.getQuestionIcon());
        String message = "Hello, " + txt + "!\n I am glad to see you.";
        Messages.showMessageDialog(project, message, "Information", Messages.getInformationIcon());
    }
}
