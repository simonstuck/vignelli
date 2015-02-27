package com.simonstuck.vignelli.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class IOUtils {
    public static String readFile(String path) throws IOException {
        return Files.toString(new File(path), Charsets.UTF_8);
    }

    public static String readFile(URI path) throws IOException {
        return Files.toString(new File(path), Charsets.UTF_8);
    }
}
