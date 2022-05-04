package com.buzas.cloud.application.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ListMessage extends AbstractMessage{

    private final List<String> files;

    public ListMessage(Path path) throws IOException {
        files = Files.list(path)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }

    public List<String> getFiles() {
        return files;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST;
    }
}
