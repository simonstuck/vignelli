package com.simonstuck.vignelli.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class IOUtils {

    private static final Logger LOG = Logger.getInstance(IOUtils.class.getName());
    private static final String PATH_PREFIX = "/classes/";

    public static String readFile(String path) throws IOException {
        File base = new File(PathUtil.getJarPathForClass(IOUtils.class));
        if (base.isDirectory()) {
            File file = new File(base.getParentFile(), PATH_PREFIX + path);
            return Files.toString(file, Charsets.UTF_8);
        } else {
            ZipFile zip = new ZipFile(base);
            ZipEntry entry = zip.getEntry(path);
            InputStream in = zip.getInputStream(entry);
            String result = convertStreamToString(in);
            in.close();
            return result;
        }
    }

    private static String convertStreamToString(InputStream in) {
        Scanner s = new Scanner(in, Charsets.UTF_8.displayName()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
