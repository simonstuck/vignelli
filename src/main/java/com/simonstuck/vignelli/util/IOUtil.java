package com.simonstuck.vignelli.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.intellij.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class IOUtil {

    private static final String PATH_PREFIX = "/classes/";

    /**
     * Reads a file from within the plugin sources.
     * @param path The relative path to the file in the plugin
     * @return The contents of the file
     * @throws IOException When the file cannot be found
     */
    public static String readFile(String path) throws IOException {
        File base = new File(PathUtil.getJarPathForClass(IOUtil.class));
        if (base.isDirectory()) {
            File file = new File(base.getParentFile(), PATH_PREFIX + path);
            return Files.toString(file, Charsets.UTF_8);
        } else {
            ZipFile zip = new ZipFile(base);
            ZipEntry entry = zip.getEntry(path);
            InputStream in = zip.getInputStream(entry);
            String result = convertStreamToString(in);
            in.close();
            zip.close();
            return result;
        }
    }

    /**
     * Tries to read a file from within the plugin sources and returns the empty string if it cannot be found.
     * @param path The relative path to the file in the plugin sources
     * @return The contents of the file, or "" if it cannot be found
     */
    public static String tryReadFile(String path) {
        try {
            return IOUtil.readFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Reads an entire {@link java.io.InputStream} into a {@link java.lang.String} that is then returned.
     * @param in The input stream to read
     * @return The entire contents of the input stream.
     */
    private static String convertStreamToString(InputStream in) {
        Scanner s = new Scanner(in, Charsets.UTF_8.displayName()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Finds the first available file with the given base name.
     * Attaches integer numbers as long as necessary until a file does not exit.
     * @param directory The base directory
     * @param basename The basename of the file.
     * @param s
     * @return The final file.
     */
    public static File getFirstAvailableFile(File directory, String basename, String extension) {
        File resultFile = new File(directory, basename + extension);
        int i = 1;
        while(resultFile.exists()) {
            resultFile = new File(directory, basename + "_" + i + extension);
            i++;
        }
        return resultFile;
    }
}
