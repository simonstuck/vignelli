package com.simonstuck.vignelli.decomposition.graph;

import static org.junit.Assert.assertTrue;

import com.simonstuck.vignelli.utils.IdGenerator;
import org.junit.Before;
import org.junit.Test;

public class GraphNodeIdGeneratorTest {
    private IdGenerator<Integer> generator;

    @Before
    public void setUp() {
        generator = new GraphNodeIdGenerator();
    }

    @Test
    public void generatesNewIdForSubsequentCalls() {
        int firstCall = generator.generateId();
        int secondCall = generator.generateId();
        assertTrue(firstCall != secondCall);
    }

}