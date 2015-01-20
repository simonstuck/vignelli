package com.simonstuck.vignelli.decomposition.graph.cfg;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.intellij.psi.PsiMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AugmentedControlFlowGraphTest {

    @Test
    public void getsParentMethodWithWhichItWasCreated() throws Exception {
        PsiMethod method = mock(PsiMethod.class);
        AugmentedControlFlowGraph cfg = new AugmentedControlFlowGraph(method);
        assertEquals(cfg.getMethod(), method);
    }
}