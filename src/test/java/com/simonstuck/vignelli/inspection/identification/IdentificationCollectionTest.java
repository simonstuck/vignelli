package com.simonstuck.vignelli.inspection.identification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class IdentificationCollectionTest {

    private IdentificationCollection<MethodChainIdentification> ids;

    @Before
    public void setUp() throws Exception {
        ids = new IdentificationCollection<MethodChainIdentification>();
    }

    @Test
    public void returnsAnIteratorOverEmptySetWhenItContainsNoIdentifications() throws Exception {
        assertFalse(ids.iterator().hasNext());
    }

    @Test
    public void shouldReturnCorrectSizeForNumberOfElementsAdded() throws Exception {
        ids.add(mock(MethodChainIdentification.class));
        ids.add(mock(MethodChainIdentification.class));
        assertEquals(2, ids.size());
    }

    @Test
    public void shouldTellWhetherAnElementIsContainedInTheCollection() throws Exception {
        MethodChainIdentification id = mock(MethodChainIdentification.class);
        ids.add(id);
        assertTrue(ids.contains(id));
        assertFalse(ids.contains(mock(MethodChainIdentification.class)));
    }

    @Test
    public void shouldAddAllItemsFromAGivenCollection() throws Exception {
        Set<MethodChainIdentification> given = new HashSet<MethodChainIdentification>();
        given.add(mock(MethodChainIdentification.class));
        given.add(mock(MethodChainIdentification.class));
        given.add(mock(MethodChainIdentification.class));
        ids.addAll(given);

        for (MethodChainIdentification id : given) {
            assertTrue(ids.contains(id));
        }
    }

    @Test
    public void shouldCollectionWithPredicate() throws Exception {
        ids.add(mock(MethodChainIdentification.class));
        ids.add(mock(MethodChainIdentification.class));

        Predicate<MethodChainIdentification> pred = new Predicate<MethodChainIdentification>() {
            @Override
            public boolean test(MethodChainIdentification methodChainIdentification) {
                return false;
            }
        };

        assertEquals(0, ids.filter(pred).size());
    }

    @Test
    public void shouldFilterCollectionWithOtherCollectionElements() throws Exception {
        MethodChainIdentification id1 = mock(MethodChainIdentification.class);

        MethodChainIdentificationCollection other = new MethodChainIdentificationCollection();
        other.add(id1);

        ids.add(id1);
        ids.add(mock(MethodChainIdentification.class));

        IdentificationCollection<MethodChainIdentification> result = ids.filterIdentifications(other);
        assertEquals(1, result.size());
        assertFalse(result.contains(id1));
    }
}