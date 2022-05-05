package com.buzas.cloud.application.model;

import java.nio.file.Path;

public class FileInfoMessage extends AbstractMessage{

    private String name;

    public FileInfoMessage(Path path) {
        this.name = path.getFileName().toString();
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_INFO;
    }
}
