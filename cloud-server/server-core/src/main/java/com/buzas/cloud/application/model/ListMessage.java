package com.buzas.cloud.application.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ListMessage extends AbstractMessage{

    private final List<String> files;
    private Path path;

    public ListMessage(Path path) throws IOException {
        this.path = path;
        files = Files.list(path)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }

    public Path getPath() {
        return path;
    }

    public List<String> getFiles() {
        return files;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST;
    }
}
