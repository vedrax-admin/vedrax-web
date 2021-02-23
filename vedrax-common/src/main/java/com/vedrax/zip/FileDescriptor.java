package com.vedrax.zip;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class FileDescriptor {

    public FileDescriptor(String fileName, byte[] content){
        Objects.requireNonNull(fileName, "name is required");
        Objects.requireNonNull(content, "content must be provided");

        this.fileName = fileName;
        this.content = content;
        this.size = content.length;
    }

    private String fileName;
    private int size;
    private byte[] content;
}
