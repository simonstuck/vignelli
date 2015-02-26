package com.simonstuck.vignelli.inspection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.Test;

public class MethodChainingInspectionTest {
    private MethodChainingInspection inspection;

    @Before
    public void setUp() throws Exception {
        inspection = new MethodChainingInspection();
    }

    @Test
    public void displayNameShouldContainTrainWreck() throws Exception {
        assertTrue(inspection.getDisplayName().contains("Train Wreck"));
    }

    @Test
    public void shouldBeEnabledByDefault() throws Exception {
        assertTrue(inspection.isEnabledByDefault());
    }
}