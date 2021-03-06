package com.simonstuck.vignelli.ui.description;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;

public class HTMLFileTemplateTest {

    @Test
    public void shouldReplaceNoKeysWithNoGivenMappings() throws Exception {
        String template = "TEXT WITH AN UNFILLED ${ENTRY} KEY";
        String unfilled = "TEXT WITH AN UNFILLED  KEY";
        assertEquals(unfilled, new HTMLFileTemplate(template).render(new HashMap<String, Object>()));
    }

    @Test
    public void shouldReplaceOneOccurrenceOfAKeyWithAGivenMapping() throws Exception {
        String expected = "TEXT WITH AN amazing KEY";
        String template = "TEXT WITH AN ${ENTRY} KEY";
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("ENTRY", "amazing");
        values.put("ENTRY2", "something that won't come up in the result");
        assertEquals(expected, new HTMLFileTemplate(template).render(values));
    }

    @Test
    public void shouldReplaceMultipleOccurencesOfAKeyWithAGivenMapping() throws Exception {
        String expected = "TEXT WITH AN amazing KEY. Isn't this amazing?";
        String template = "TEXT WITH AN ${ENTRY} KEY. Isn't this ${ENTRY}?";
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("ENTRY", "amazing");
        assertEquals(expected, new HTMLFileTemplate(template).render(values));
    }

    @Test
    public void shouldReplaceMultipleKeys() throws Exception {
        String expected = "TEXT WITH AN amazing KEY. Isn't this amazing? thank you.";
        String template = "TEXT WITH AN ${ENTRY} KEY. Isn't this ${ENTRY}? ${THANKS}.";
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("ENTRY", "amazing");
        values.put("THANKS", "thank you");
        assertEquals(expected, new HTMLFileTemplate(template).render(values));
    }
}