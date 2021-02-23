package com.vedrax.util;

import com.vedrax.zip.FileDescriptor;
import org.apache.commons.lang3.Validate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static byte[] createZip(List<FileDescriptor> files) throws IOException {
        Validate.notEmpty(files, "files must be provided");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);

        for (FileDescriptor file : files) {
            ZipEntry entry = new ZipEntry(file.getFileName());
            entry.setSize(file.getSize());
            zos.putNextEntry(entry);
            zos.write(file.getContent());
        }

        zos.closeEntry();
        zos.close();
        return byteArrayOutputStream.toByteArray();
    }

}
