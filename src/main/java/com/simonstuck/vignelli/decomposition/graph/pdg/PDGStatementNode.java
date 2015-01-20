package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import com.simonstuck.vignelli.decomposition.graph.cfg.CFGNode;

import java.util.Set;

public class PDGStatementNode extends PDGNode {
    public PDGStatementNode(int id, CFGNode cfgNode, final Set<PsiLocalVariable> localVariables) {
        super(id);
        cfgNode.getStatement().accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                if (expression.getParent() instanceof PsiAssignmentExpression) {
                    PsiAssignmentExpression parent = (PsiAssignmentExpression) expression.getParent();
                    PsiElement resolved = expression.resolve();
                    if (resolved instanceof PsiLocalVariable) {
                        PsiLocalVariable var = (PsiLocalVariable) resolved;
                        if (expression == parent.getLExpression()) {
                            // We must be talking about written variable
                            definedVariables.add(var);
                        } else {
                            // we are talking about reading a variable
                            usedVariables.add(var);
                        }
                    }
                }
                System.out.println("Def: " + definedVariables);
                System.out.println("used: " + usedVariables);
            }
        });
    }


}
