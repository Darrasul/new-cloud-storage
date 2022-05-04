package com.buzas.cloud.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadMessage extends AbstractMessage{

    private String name;
    private byte[] bytes;

    public DownloadMessage(Path path) throws IOException {
        name = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DOWNLOAD;
    }
}
