package com.simonstuck.vignelli.psi.util;

import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiElement;

public class LineUtil {
    public static int countLines(PsiElement element) {
        if (element instanceof PsiCompiledElement) {
            return 0;
        }
        return countLinesOfCode(element.getText());
    }

    public static int countCommentLines(PsiElement element) {
        if (element instanceof PsiCompiledElement) {
            return 0;
        }
        return countCommentLines(element.getText());
    }

    private static int countCommentLines(String code) {
        String[] lines = splitOnNewLines(code);
        int count = 0;
        boolean longComment = false;
        for (String line : lines) {
            if (line.startsWith("//")) {
                count++;
            }

            if (line.contains("/*")) {
                longComment = true;
            }
            if (longComment) {
                count++;
                continue;
            }
            if (longComment && line.contains("*/")) {
                longComment = false;
            }
        }
        return count;
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
