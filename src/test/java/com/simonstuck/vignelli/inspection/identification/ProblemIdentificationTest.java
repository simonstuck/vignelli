package com.simonstuck.vignelli.inspection.identification;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.codeInspection.CommonProblemDescriptor;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;

import org.junit.Test;

public class ProblemIdentificationTest {
    @Test
    public void shouldBeEqualToAnotherProblemIdentificationUsingASimilarProblemDescriptor() throws Exception {
        ProblemIdentificationBuilder builder = new ProblemIdentificationBuilder();
        ProblemDescriptor problemDescriptor = createProblemDescriptor();
        ProblemIdentification identificationA = builder.setName("Problem")
                .setShortDescription("short description")
                .setLongDescription("long description")
                .setProblemDescriptor(problemDescriptor)
                .build();

        ProblemIdentification identificationB = builder.setName("Problem")
                .setShortDescription("short description")
                .setLongDescription("long description")
                .setProblemDescriptor(problemDescriptor)
                .build();

        assertTrue(identificationA.equals(identificationB));
    }

    @Test
    public void shouldBeUnequalToAnotherProblemIdentificationUsingASimilarProblemDescriptorButDifferentName() throws Exception {
        ProblemIdentificationBuilder builder = new ProblemIdentificationBuilder();
        ProblemDescriptor problemDescriptor = createProblemDescriptor();
        ProblemIdentification identificationA = builder.setName("ProblemA")
                .setShortDescription("short description")
                .setLongDescription("long description")
                .setProblemDescriptor(problemDescriptor)
                .build();

        ProblemIdentification identificationB = builder.setName("ProblemB")
                .setShortDescription("short description")
                .setLongDescription("long description")
                .setProblemDescriptor(problemDescriptor)
                .build();

        assertFalse(identificationA.equals(identificationB));
    }

    private ProblemDescriptor createProblemDescriptor() {
        PsiElement element = mock(PsiElement.class);
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