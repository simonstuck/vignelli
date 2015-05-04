package com.simonstuck.vignelli.evaluation;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.simonstuck.vignelli.evaluation.datamodel.ClassEval;
import com.simonstuck.vignelli.evaluation.datamodel.MethodEval;
import com.simonstuck.vignelli.evaluation.datamodel.ProjectEval;
import com.simonstuck.vignelli.psi.util.LineUtil;
import com.simonstuck.vignelli.psi.util.MetricsUtil;
import com.simonstuck.vignelli.psi.util.NavigationUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class RateMethodsAnAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(RateMethodsAnAction.class.getName());

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        assert project != null;

        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        String[] classNames = cache.getAllClassNames();
        Collection<PsiClass> classes = getClasses(project, cache, classNames);

        ProjectEval projectEval = new ProjectEval(project.getName());

        for (PsiClass clazz : classes) {
            Optional<ClassEval> optClassRatings = rateClass(clazz);
            if (!optClassRatings.isPresent()) {
                return;
            } else {
                projectEval.addClassEval(optClassRatings.get());
            }
        }
        Gson gson = new Gson();
        LOG.info(gson.toJson(projectEval));
    }

    private Collection<PsiClass> getClasses(Project project, PsiShortNamesCache cache, String[] classNames) {
        Collection<PsiClass> classes = new HashSet<PsiClass>();
        for (String className : classNames) {
            PsiClass[] theClasses = cache.getClassesByName(className, GlobalSearchScope.projectScope(project));
            classes.addAll(Arrays.asList(theClasses));
        }
        return classes;
    }

    private Optional<ClassEval> rateClass(PsiClass clazz) {
        LOG.info(clazz.getQualifiedName());
        ClassEval classEval = new ClassEval(clazz.getQualifiedName());

        @SuppressWarnings("unchecked")
        Collection<PsiMethod> methods = PsiTreeUtil.collectElementsOfType(clazz, PsiMethod.class);
        for (PsiMethod method : methods) {
            int loc = LineUtil.countLines(method.getBody());
            int commentLines = LineUtil.countCommentLines(method.getBody());
            int cyclomaticComplexity = MetricsUtil.getCyclomaticComplexity(method);
            PsiParameterList parameterList = method.getParameterList();
            int numParameters = parameterList.getParametersCount();
            int nestedBlockDepth = MetricsUtil.getNestedBlockDepth(method);
            String[] options = new String[] { "complex", "not complex", "cancel", "stop and save"};

            NavigationUtil.navigateToElement(method);

            int selection = Messages.showDialog(clazz.getQualifiedName() + ":" + method.getName(), "Is This Method Complex?", options, 1, 0, Messages.getQuestionIcon(), null);

            if (selection == 2) {
                // cancel
                return Optional.absent();
            } else if (selection == 3) {
                // stop and save
                return Optional.of(classEval);
            }

            boolean isComplex = selection == 0;
            classEval.addMethodEval(new MethodEval(method.getName(), loc, cyclomaticComplexity, numParameters, nestedBlockDepth, commentLines, isComplex));
        }
        return Optional.of(classEval);
    }
}
