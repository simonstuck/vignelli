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
import com.simonstuck.vignelli.evaluation.datamodel.MethodChainData;
import com.simonstuck.vignelli.evaluation.impl.ClassMethodChainsLister;
import com.simonstuck.vignelli.evaluation.io.AtomicallyReplacingFileWriter;
import com.simonstuck.vignelli.util.IOUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MethodChainsListAnAction extends AnAction  {

    private static final Logger LOG = Logger.getInstance(MethodChainsListAnAction.class.getName());
    private static final String RESULTS_FILE_BASENAME = "method_chains_list";
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
        Collection<PsiClass> classes = getClasses(project, cache, cache.getAllClassNames());
        PsiElementEvaluator.EvaluationResult.Outcome currentOutcome = PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED;

        Iterator<PsiClass> classIterator = classes.iterator();

        List<MethodChainData> chainData = new LinkedList<MethodChainData>();

        while (classIterator.hasNext() && currentOutcome == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
            PsiClass clazz = classIterator.next();

            ClassMethodChainsLister lister = new ClassMethodChainsLister();
            PsiElementEvaluator.EvaluationResult<List<MethodChainData>> list = lister.evaluate(clazz);

            if (list.getOutcome() == PsiElementEvaluator.EvaluationResult.Outcome.COMPLETED) {
                chainData.addAll(list.getEvaluation());
            }

            String jsonResults = gson.toJson(chainData);
            LOG.info(jsonResults);
            try {
                resultsWriter.replaceWith(jsonResults);
            } catch (IOException e) {
                LOG.info("Could not replace contents", e);
            }

            currentOutcome = list.getOutcome();
        }

        Messages.showDialog(project, "Method Chain List Written.", "List Method Chains.", new String[]{"OK"}, 0, Messages.getInformationIcon());

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
