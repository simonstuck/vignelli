package com.simonstuck.vignelli.testutils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.intellij.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;
import java.util.zip.ZipFile;

public final class IOUtils {
    public static String readFile(String path) throws IOException {
        return Files.toString(new File(path), Charsets.UTF_8);
    }
}
