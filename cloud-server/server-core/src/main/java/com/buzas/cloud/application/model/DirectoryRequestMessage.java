package com.buzas.cloud.application.model;

import java.nio.file.Path;

public class DirectoryRequestMessage extends AbstractMessage{

    String name;

    public DirectoryRequestMessage(Path path) {
        this.name = path.getFileName().toString();
    }

    public DirectoryRequestMessage(String path){
        this.name = path;
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DIRECTORY_REQUEST;
    }
}
