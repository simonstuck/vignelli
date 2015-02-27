package com.simonstuck.vignelli.ui.description;

import java.util.Map;

public class HTMLFileTemplate implements Template {

    private final String template;

    public HTMLFileTemplate(String template) {
        this.template = template;
    }

    @Override
    public String render(Map<String, String> content) {
        String result = template;
        for (Map.Entry<String,String> entry : content.entrySet()) {
            result = result.replaceAll("\\{\\{" + entry.getKey() + "\\}\\}", entry.getValue());
        }
        return result;
    }
}