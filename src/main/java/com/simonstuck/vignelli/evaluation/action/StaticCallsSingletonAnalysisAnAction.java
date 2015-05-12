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
import com.simonstuck.vignelli.evaluation.datamodel.ClassSingletonUseClassification;
import com.simonstuck.vignelli.evaluation.datamodel.ProjectStaticCallsSingletonClassifications;
import com.simonstuck.vignelli.evaluation.datamodel.SingletonClassClassification;
import com.simonstuck.vignelli.evaluation.impl.ClassSingletonUseClassifier;
import com.simonstuck.vignelli.evaluation.impl.IntelliJManualUserSingletonClassifier;
import com.simonstuck.vignelli.evaluation.io.AtomicallyReplacingFileWriter;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class StaticCallsSingletonAnalysisAnAction extends AnAction  {

    private static final Logger LOG = Logger.getInstance(StaticCallsSingletonAnalysisAnAction.class.getName());
    private static final String RESULTS_FILE_BASENAME = "singleton_analysis_ratings";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        assert project != null;


        File directory = chooseDirectory(project);
        File resultsFile = IOUtil.getFirstAvailableFile(directory, RESULTS_FILE_BASENAME, JSON_EXTENSION);

        ProjectStaticCallsSingletonClassifications projectStaticCallsSingletonClassifications = new ProjectStaticCallsSingletonClassifications(project.getName());

        AtomicallyReplacingFileWriter resultsWriter = new AtomicallyReplacingFileWriter(resultsFile);
        Gson gson = new Gson();


        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        Collection<PsiClass> classes = getClasses(project, cache, cache.getAllClassNames());
        PsiElementEvaluator.EvaluationResult.Outcome currentOutcome = PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED;

        Iterator<PsiClass> classIterator = classes.iterator();
        while (classIterator.hasNext() && currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
            PsiClass clazz = classIterator.next();

            PsiElementEvaluator<SingletonClassClassification> classIsSingletonClassifier = new IntelliJManualUserSingletonClassifier();
            ClassSingletonUseClassifier classifier = new ClassSingletonUseClassifier(project, classIsSingletonClassifier);
            PsiElementEvaluator.EvaluationResult<ClassSingletonUseClassification> classificationResult = classifier.evaluate(clazz);

            if (classificationResult.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                projectStaticCallsSingletonClassifications.addClassSingletonUseClassification(classificationResult.getEvaluation());
            }

            String jsonResults = gson.toJson(projectStaticCallsSingletonClassifications);
            LOG.info(jsonResults);
            try {
                resultsWriter.replaceWith(jsonResults);
            } catch (IOException e) {
                LOG.info("Could not replace contents", e);
            }

            currentOutcome = classificationResult.getOutcome();
        }
        Messages.showDialog(project, "Singleton Classification finished", "Singleton Classification", new String[]{"OK"}, 0, Messages.getInformationIcon());

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
