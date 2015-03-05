package com.simonstuck.vignelli.inspection.identification;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import com.simonstuck.vignelli.inspection.TrainWreckVariableImprovementOpportunity;

import org.junit.Test;

import java.util.Optional;

public class ProblemIdentificationTest {
    @Test
    public void shouldBeEqualToAnotherProblemIdentificationUsingASimilarProblemDescriptor() throws Exception {
        ProblemDescriptor problemDescriptor = createProblemDescriptor(createProblemElement());
        ProblemIdentification identificationA = new ProblemIdentification(problemDescriptor,"Problem");
        ProblemIdentification identificationB = new ProblemIdentification(problemDescriptor,"Problem");

        assertTrue(identificationA.equals(identificationB));
        assertEquals(identificationA.hashCode(), identificationB.hashCode());
    }

    @Test
    public void shouldBeUnequalToAnotherProblemIdentificationUsingASimilarProblemDescriptorButDifferentName() throws Exception {
        ProblemDescriptor problemDescriptor = createProblemDescriptor(createProblemElement());
        ProblemIdentification identificationA = new ProblemIdentification(problemDescriptor,"ProblemA");
        ProblemIdentification identificationB = new ProblemIdentification(problemDescriptor,"ProblemB");

        assertFalse(identificationA.equals(identificationB));
    }

    @Test
    public void shouldHaveNoImprovementOpportunityIfResultIsNotAssignedToVariable() throws Exception {
        ProblemDescriptor problemDescriptor = createProblemDescriptor(createProblemElement());
        ProblemIdentification identification = new ProblemIdentification(problemDescriptor, "Problem");
        assertEquals(Optional.<TrainWreckVariableImprovementOpportunity>empty(), identification.improvementOpportunity());
    }

    @Test
    public void shouldHaveImprovementOpportunityIfResultIsAssignedToVariable() throws Exception {
        PsiElement element = createProblemElement();
        when(element.getParent()).thenReturn(mock(PsiVariable.class));
        ProblemDescriptor problemDescriptor = createProblemDescriptor(element);
        ProblemIdentification identification = new ProblemIdentification(problemDescriptor, "Problem");
        Optional<TrainWreckVariableImprovementOpportunity> opportunity = identification.improvementOpportunity();
        verify(element).getParent();
        assertTrue(opportunity.isPresent());
    }

    private ProblemDescriptor createProblemDescriptor(PsiElement element) {
        ProblemDescriptor problemDescriptor = mock(ProblemDescriptor.class);
        when(problemDescriptor.getLineNumber()).thenReturn(1);
        when(problemDescriptor.getStartElement()).thenReturn(element);
        when(problemDescriptor.getEndElement()).thenReturn(element);
        when(problemDescriptor.getPsiElement()).thenReturn(element);
        when(problemDescriptor.getHighlightType()).thenReturn(ProblemHighlightType.GENERIC_ERROR);
        when(problemDescriptor.getDescriptionTemplate()).thenReturn("TEMPLATE");
        return problemDescriptor;
    }

    private PsiElement createProblemElement() {
        PsiElement element = mock(PsiElement.class);
        PsiFile containingFile = mock(PsiFile.class);
        when(containingFile.getVirtualFile()).thenReturn(mock(VirtualFile.class));
        when(element.getContainingFile()).thenReturn(containingFile);
        return element;
    }
}