package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.decomposition.ast.visitor.LocalVariableDeclarationCollectorVisitor;
import com.simonstuck.vignelli.decomposition.graph.cfg.CFGNode;

import java.util.Set;

public class PDGStatementNode extends PDGNode {

    /**
     * Creates a new PDG statement node.
     * @param id The unique id to use for this node
     * @param cfgNode The associated cfg node
     * @param localVariables The local variables of this method
     */
    public PDGStatementNode(final int id, CFGNode cfgNode, final Set<PsiLocalVariable> localVariables) {
        super(id);
        cfgNode.getStatement().accept(new JavaRecursiveElementVisitor() {

            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);

                PsiElement resolved = expression.resolve();

                if (expression.getParent() instanceof PsiAssignmentExpression) {
                    PsiAssignmentExpression parent = (PsiAssignmentExpression) expression.getParent();
                    if (resolved instanceof PsiVariable) {
                        PsiVariable var = (PsiVariable) resolved;
                        if (expression == parent.getLExpression()) {
                            // We must be talking about written variable
                            definedVariables.add(var);
                        } else {
                            usedVariables.add(var);
                        }
                    }
                } else if (resolved instanceof PsiVariable) {
                    usedVariables.add((PsiVariable) resolved);
                }
            }

            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                LocalVariableDeclarationCollectorVisitor visitor = new LocalVariableDeclarationCollectorVisitor();
                visitor.visitStatement(statement);
                declaredVariables.addAll(visitor.getLocalVariables());
                for (PsiElement element : statement.getDeclaredElements()) {
                    if (element instanceof PsiLocalVariable) {
                        PsiExpression initExpression = ((PsiLocalVariable) element).getInitializer();
                        if (initExpression != null) {
                            visitExpression(initExpression);
                            definedVariables.addAll(visitor.getLocalVariables());
                        }
                    }
                }
            }
        });
    }
}
