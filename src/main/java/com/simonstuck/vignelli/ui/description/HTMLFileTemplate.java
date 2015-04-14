package com.simonstuck.vignelli.ui.description;

import com.floreysoft.jmte.Engine;

import java.util.HashMap;

public class HTMLFileTemplate implements Template {

    private final String template;

    public HTMLFileTemplate(String template) {
        this.template = template;
    }

    @Override
    public String render(HashMap<String, Object> content) {
        Engine engine = new Engine();
        return engine.transform(template, content);
    }
}