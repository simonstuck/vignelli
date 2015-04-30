package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiElement;

public class LineUtil {
    public static int countLines(PsiElement element) {
        if (element instanceof PsiCompiledElement) {
            return 0;
        }
        return countLinesOfCode(element.getText());
    }

    private static int countLinesOfCode(String code) {
        String[] lines = splitOnNewLines(code);
        int count = 0;
        for (String line : lines) {
            if (line.trim().length() > 0) {
                count++;
            }
        }
        return count;
    }

    private static String[] splitOnNewLines(String code) {
        return code.split("\\r?\\n");
    }
}
