package com.simonstuck.vignelli.ui.description;

import java.util.Map;

public interface Template {
    String render(Map<String, Object> content);
}
