package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
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
    public PDGStatementNode(int id, CFGNode cfgNode, final Set<PsiLocalVariable> localVariables) {
        super(id);
        cfgNode.getStatement().accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                // FIXME: use of instanceof is not good
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

            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                super.visitDeclarationStatement(statement);
                LocalVariableDeclarationCollectorVisitor visitor = new LocalVariableDeclarationCollectorVisitor();
                visitor.visitStatement(statement);
                declaredVariables.addAll(visitor.getLocalVariables());
                System.out.println("Decl: " + declaredVariables);
            }
        });
    }


}
