package com.buzas.cloud.application.model;

import java.nio.file.Path;

public class DirectoryAnswerMessage extends AbstractMessage{

    boolean isDirectory;
    boolean isMain;
    String name;

    public DirectoryAnswerMessage(boolean isDirectory, Path path, boolean isMain) {
        this.isDirectory = isDirectory;
        this.name = path.getFileName().toString();
        this.isMain = isMain;
    }

    public boolean isMain() {
        return isMain;
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
