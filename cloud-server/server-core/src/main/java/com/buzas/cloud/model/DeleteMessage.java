package com.buzas.cloud.model;

import java.nio.file.Path;

public class DeleteMessage extends AbstractMessage{

    private String name;

    public DeleteMessage(Path path) {
        name = path.getFileName().toString();
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
