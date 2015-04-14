package com.simonstuck.vignelli.ui.description;

import java.util.HashMap;
import java.util.Map;

public interface Template {
    String render(HashMap<String, Object> content);
}
