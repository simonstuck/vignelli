package com.simonstuck.vignelli.decomposition.ast.util;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.simonstuck.vignelli.decomposition.ast.visitor.LocalVariableDeclarationCollectorVisitor;

import java.util.Set;

public class PsiMethodUtil {

    /**
     * Gets all declared local variables for the given method.
     * @param method The method for which to find local variables
     * @return A set with all local variables
     */
    public static Set<PsiLocalVariable> getLocalVariables(PsiMethod method) {
        LocalVariableDeclarationCollectorVisitor variableCollector = new LocalVariableDeclarationCollectorVisitor();
        variableCollector.visitMethod(method);
        return variableCollector.getLocalVariables();
    }
}
