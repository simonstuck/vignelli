package com.simonstuck.vignelli.refactoring;

import static org.mockito.Mockito.mock;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;

import org.junit.Test;

public class TrainWreckVariableRefactoringImplTest {


    @Test
    public void shouldInstantiateInlineVariableRefactoringStep() throws Exception {
        new TrainWreckVariableRefactoringImpl(mock(PsiElement.class), mock(PsiLocalVariable.class)).nextStep();
    }
}