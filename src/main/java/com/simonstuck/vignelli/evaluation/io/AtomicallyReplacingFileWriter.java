package com.simonstuck.vignelli.evaluation.io;

import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class AtomicallyReplacingFileWriter {

    private static final Logger LOG = Logger.getInstance(AtomicallyReplacingFileWriter.class.getName());

    public static final String TEMP_FILE_PREFIX = "atomically_replacing_writer_file_pref";
    public static final String TEMP_FILE_SUFFIX = "";
    private File targetFile;

    public AtomicallyReplacingFileWriter(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * Atomically replace contents of the file with the new given contents.
     * <p>Note that this method does not ensure atomicity on Windows!</p>
     * @param contents The new contents for the file
     * @throws IOException Thrown when the contents cannot be replaced.
     */
    public void replaceWith(String contents) throws IOException {
        FileLock lock = null;
        try {
            FileWriter fileWriter = null;
            File tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            try {
                fileWriter = new FileWriter(tempFile);
                fileWriter.write(contents);
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        LOG.info(e);
                    }
                }
            }

            FileChannel channel = new RandomAccessFile(targetFile, "rw").getChannel();
            lock = channel.lock();
            boolean deleted = targetFile.delete();
            boolean renamed = tempFile.renameTo(targetFile);

            if (!deleted || !renamed) {
                throw new IOException("Could not rename temporary file to new file");
            }
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    LOG.info(e);
                }
            }
        }
    }
}
