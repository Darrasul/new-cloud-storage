package com.buzas.cloud.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage{
    private String name;
    private byte[] bytes;

    public FileMessage(Path path) throws IOException {
        name = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
