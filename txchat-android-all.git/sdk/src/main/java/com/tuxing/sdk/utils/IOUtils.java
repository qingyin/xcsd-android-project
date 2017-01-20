package com.tuxing.sdk.utils;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Alan on 2015/7/9.
 */
public class IOUtils {
    private static final int BUFFER_SIZE = 4096;

    public static void copyFile(File from, File to) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(from));
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
            try {
                copyAllBytes(in, out);
            } finally {
                safeClose(out);
            }
        } finally {
            safeClose(in);
        }
    }

    public static void copyDir(File from, File to) throws IOException {
        if(from.listFiles() != null) {
            if(!to.exists()){
                to.mkdirs();
            }

            for (File fromFile : from.listFiles()) {
                File toFile = new File(to, fromFile.getName());
                if (fromFile.isFile()) {
                    copyFile(fromFile, toFile);
                } else if (fromFile.isDirectory()) {
                    toFile.mkdirs();
                    copyDir(fromFile, toFile);
                }
            }
        }
    }

    public static void zipFile(File dir, File zippedFile) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zippedFile), BUFFER_SIZE));
        zipOut.setLevel(Deflater.BEST_COMPRESSION);
        try {
            for (File file : dir.listFiles()) {
                zipFile(file, dir, zipOut);
            }
        }finally {
            safeClose(zipOut);
        }
    }

    private static void zipFile(File file, File baseDir, ZipOutputStream zipOut) throws IOException {
        if(file.isFile()){
            zipOut.putNextEntry(new ZipEntry(file.getPath().substring(baseDir.getPath().length())));
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            try {
                copyAllBytes(in, zipOut);
            }finally {
                safeClose(in);
            }
        }else if(file.isDirectory()){
            for (File child : file.listFiles()) {
                zipFile(child, baseDir, zipOut);
            }
        }
    }

    private static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }

    private static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    private static void safeClose(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    public static void deleteFile(File file) {
        if(file.isDirectory()){
            for(File child : file.listFiles()){
                deleteFile(child);
            }
            file.delete();
        }else if(file.isFile()){
            file.delete();
        }
    }
}
