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
import com.simonstuck.vignelli.evaluation.datamodel.ClassMetrics;
import com.simonstuck.vignelli.evaluation.datamodel.ProjectMetrics;
import com.simonstuck.vignelli.evaluation.impl.ClassMetricsCollector;
import com.simonstuck.vignelli.evaluation.io.QuietFileWriter;
import com.simonstuck.vignelli.psi.impl.IntelliJPsiElementCollector;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectMetricsAnAction extends AnAction  {

    private static final Logger LOG = Logger.getInstance(CollectMetricsAnAction.class.getName());
    private static final String RESULTS_FILE_BASENAME = "metrics";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        assert project != null;


        File directory = chooseDirectory(project);
        File resultsFile = IOUtil.getFirstAvailableFile(directory, RESULTS_FILE_BASENAME, JSON_EXTENSION);
        QuietFileWriter resultsWriter = null;
        try {
            resultsWriter = new QuietFileWriter(resultsFile);
            Gson gson = new Gson();

            PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
            String[] classNames = cache.getAllClassNames();
            Collection<PsiClass> classes = getClasses(project, cache, classNames);

            ProjectMetrics projectMetrics = new ProjectMetrics(project.getName());

            for (PsiClass clazz : classes) {
                ClassMetricsCollector classMetricsCollector = new ClassMetricsCollector(clazz, new IntelliJPsiElementCollector());
                PsiElementEvaluator.EvaluationResult<ClassMetrics> evaluationResult = classMetricsCollector.invoke();

                if (evaluationResult.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                    projectMetrics.addClassMetrics(evaluationResult.getEvaluation());
                }
            }


            String jsonResults = gson.toJson(projectMetrics);
            LOG.info(jsonResults);
            resultsWriter.write(jsonResults);
            Messages.showDialog(project,"Method Rating finished","Method Rating",new String[] {"OK"},0,Messages.getInformationIcon());
        } finally {
            if (resultsWriter != null) {
                resultsWriter.close();
            }
        }
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
            classes.addAll(getNonInterfaceClasses(theClasses));
        }
        return classes;
    }

    private Collection<PsiClass> getNonInterfaceClasses(@NotNull PsiClass[] classes) {
        Set<PsiClass> result = new HashSet<PsiClass>();
        for (PsiClass clazz : classes) {
            if (!clazz.isInterface()) {
                result.add(clazz);
            }
        }
        return result;
    }
}
