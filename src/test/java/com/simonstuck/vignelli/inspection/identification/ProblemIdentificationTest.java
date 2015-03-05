package com.simonstuck.vignelli.inspection.identification;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import org.junit.Test;

public class ProblemIdentificationTest {
    @Test
    public void shouldBeEqualToAnotherProblemIdentificationUsingASimilarProblemDescriptor() throws Exception {
        ProblemDescriptor problemDescriptor = createProblemDescriptor();
        ProblemIdentification identificationA = new ProblemIdentification(problemDescriptor,"Problem");
        ProblemIdentification identificationB = new ProblemIdentification(problemDescriptor,"Problem");

        assertTrue(identificationA.equals(identificationB));
        assertEquals(identificationA.hashCode(), identificationB.hashCode());
    }

    @Test
    public void shouldBeUnequalToAnotherProblemIdentificationUsingASimilarProblemDescriptorButDifferentName() throws Exception {
        ProblemDescriptor problemDescriptor = createProblemDescriptor();
        ProblemIdentification identificationA = new ProblemIdentification(problemDescriptor,"ProblemA");
        ProblemIdentification identificationB = new ProblemIdentification(problemDescriptor,"ProblemB");

        assertFalse(identificationA.equals(identificationB));
    }

    private ProblemDescriptor createProblemDescriptor() {
        PsiElement element = mock(PsiElement.class);

        PsiFile containingFile = mock(PsiFile.class);
        when(containingFile.getVirtualFile()).thenReturn(mock(VirtualFile.class));
        when(element.getContainingFile()).thenReturn(containingFile);
        ProblemDescriptor problemDescriptor = mock(ProblemDescriptor.class);
        when(problemDescriptor.getLineNumber()).thenReturn(1);
        when(problemDescriptor.getStartElement()).thenReturn(element);
        when(problemDescriptor.getEndElement()).thenReturn(element);
        when(problemDescriptor.getPsiElement()).thenReturn(element);
        when(problemDescriptor.getHighlightType()).thenReturn(ProblemHighlightType.GENERIC_ERROR);
        when(problemDescriptor.getDescriptionTemplate()).thenReturn("TEMPLATE");
        return problemDescriptor;
    }
}