package com.simonstuck.vignelli.evaluation.action;

import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.simonstuck.vignelli.psi.PsiElementCollector;
import com.simonstuck.vignelli.evaluation.PsiElementEvaluator;
import com.simonstuck.vignelli.evaluation.datamodel.ClassEval;
import com.simonstuck.vignelli.evaluation.datamodel.MethodClassification;
import com.simonstuck.vignelli.evaluation.datamodel.ProjectEval;
import com.simonstuck.vignelli.evaluation.impl.ClassEvaluator;
import com.simonstuck.vignelli.evaluation.impl.IntelliJMethodClassifier;
import com.simonstuck.vignelli.psi.impl.IntelliJPsiElementCollector;
import com.simonstuck.vignelli.evaluation.io.QuietFileWriter;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class RateMethodsAnAction extends AnAction  {

    private static final Logger LOG = Logger.getInstance(RateMethodsAnAction.class.getName());
    private static final String RESULTS_FILE_BASENAME = "method_ratings";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        assert project != null;

        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        String[] classNames = cache.getAllClassNames();
        Collection<PsiClass> classes = getClasses(project, cache, classNames);

        ProjectEval projectEval = new ProjectEval(project.getName());

        Iterator<PsiClass> classIterator = classes.iterator();
        PsiElementEvaluator.EvaluationResult.Outcome currentOutcome = PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED;

        while (currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED && classIterator.hasNext()) {
            PsiClass clazz = classIterator.next();
            PsiElementCollector methodCollector = new IntelliJPsiElementCollector();
            PsiElementEvaluator<MethodClassification> methodClassifier = new IntelliJMethodClassifier();
            ClassEvaluator classEvaluator = new ClassEvaluator(clazz, methodCollector, methodClassifier);
            PsiElementEvaluator.EvaluationResult<ClassEval> classEval = classEvaluator.invoke();

            if (classEval.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                projectEval.addClassEval(classEval.getEvaluation());
            }

            currentOutcome = classEval.getOutcome();
        }

        if (outcomeRequiresDataSave(currentOutcome)) {
            File directory = chooseDirectory(project);
            File resultsFile = IOUtil.getFirstAvailableFile(directory, RESULTS_FILE_BASENAME, JSON_EXTENSION);

            QuietFileWriter resultsWriter = new QuietFileWriter(resultsFile);
            Gson gson = new Gson();
            String jsonResults = gson.toJson(projectEval);
            LOG.info(jsonResults);
            resultsWriter.write(jsonResults);
            resultsWriter.close();
        }

    }

    private boolean outcomeRequiresDataSave(PsiElementEvaluator.EvaluationResult.Outcome currentOutcome) {
        return currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED || currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.CANCELLED_WITH_SAVE;
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
            classes.addAll(Arrays.asList(theClasses));
        }
        return classes;
    }
}
