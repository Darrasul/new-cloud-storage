package com.buzas.cloud.application.model;

import java.nio.file.Path;

public class DirectoryAnswerMessage extends AbstractMessage{

    boolean isDirectory;
    String name;

    public DirectoryAnswerMessage(boolean isDirectory, Path path) {
        this.isDirectory = isDirectory;
        this.name = path.getFileName().toString();
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DIRECTORY_ANSWER;
    }
}
