package com.simonstuck.vignelli.evaluation.io;

import com.intellij.openapi.diagnostic.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <p>The {@link com.simonstuck.vignelli.evaluation.io.QuietFileWriter} is a convenience class
 * for quietly writing data out to a given file.</p>
 * <p>Any errors that occur during this process are logged at INFO log level but no further action
 * is taken. Any lost data is also logged at INFO level.</p>
 */
public class QuietFileWriter implements Closeable {

    private static final Logger LOG = Logger.getInstance(QuietFileWriter.class.getName());

    private FileWriter writer;

    public QuietFileWriter(@NotNull File file) {
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            LOG.info(e);
        }
    }

    /**
     * Writes the given contents to the file.
     * @param contents The contents to write to the file.
     */
    public void write(String contents) {
        if (writer == null) {
            LOG.info("could not write contents to file: " + contents);
        }

        try {
            writer.write(contents);
        } catch (IOException e) {
            LOG.info(e);
        }
    }


    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                LOG.info("writer could not be closed", e);
            }
        }
    }
}
