package com.simonstuck.vignelli.evaluation.action;

import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassMethodClassifications;
import com.simonstuck.vignelli.evaluation.datamodel.ProjectMethodClassifications;
import com.simonstuck.vignelli.evaluation.impl.ClassMethodClassifier;
import com.simonstuck.vignelli.evaluation.impl.IntelliJManualUserMethodClassifier;
import com.simonstuck.vignelli.evaluation.io.AtomicallyReplacingFileWriter;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class RateComplexMethodsAnAction extends AnAction  {

    private static final Logger LOG = Logger.getInstance(RateComplexMethodsAnAction.class.getName());
    private static final String RESULTS_FILE_BASENAME = "complex_method_ratings";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        assert project != null;


        File directory = chooseDirectory(project);
        File resultsFile = IOUtil.getFirstAvailableFile(directory, RESULTS_FILE_BASENAME, JSON_EXTENSION);
        AtomicallyReplacingFileWriter resultsWriter = new AtomicallyReplacingFileWriter(resultsFile);
        Gson gson = new Gson();

        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        String[] classNames = cache.getAllClassNames();
        Collection<PsiClass> classes = getClasses(project, cache, classNames);

        ProjectMethodClassifications projectMethodClassifications = new ProjectMethodClassifications(project.getName());

        Iterator<PsiClass> classIterator = classes.iterator();
        PsiElementEvaluator.EvaluationResult.Outcome currentOutcome = PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED;

        while (currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED && classIterator.hasNext()) {
            PsiClass clazz = classIterator.next();

            ClassMethodClassifier classMethodClassifier = new ClassMethodClassifier(clazz, new IntelliJManualUserMethodClassifier());
            PsiElementEvaluator.EvaluationResult<ClassMethodClassifications> evaluationResult = classMethodClassifier.invoke();

            if (evaluationResult.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                projectMethodClassifications.addClassMethodClassification(evaluationResult.getEvaluation());
            }

            String jsonResults = gson.toJson(projectMethodClassifications);
            LOG.info(jsonResults);
            try {
                resultsWriter.replaceWith(jsonResults);
            } catch (IOException e) {
                LOG.info("Could not replace contents", e);
            }

            currentOutcome = evaluationResult.getOutcome();
        }
        Messages.showDialog(project,"Method Rating finished","Method Rating",new String[] {"OK"},0,Messages.getInformationIcon());
    }


    @Nullable
    private File chooseDirectory(@NotNull Project project) {

        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false,false,false,false);
        VirtualFile chosenVirtualFile = FileChooser.chooseFile(fileChooserDescriptor, project, null);
        if (chosenVirtualFile != null) {
            return new File(chosenVirtualFile.getPath());
        } else {
            return null;
        }
    }

    private Collection<PsiClass> getClasses(Project project, PsiShortNamesCache cache, String[] classNames) {
        Collection<PsiClass> classes = new HashSet<PsiClass>();
        for (String className : classNames) {
            PsiClass[] theClasses = cache.getClassesByName(className, GlobalSearchScope.projectScope(project));
            for (PsiClass clazz : theClasses) {
                if (!clazz.isInterface()) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }
}
