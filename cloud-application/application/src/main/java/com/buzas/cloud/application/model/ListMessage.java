package com.buzas.cloud.application.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ListMessage extends AbstractMessage{

    private final List<String> fileNames;
    private List<String> fileSizes;

    public ListMessage(Path path) throws IOException {
        fileNames = Files.list(path)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
        fileSizes = new ArrayList<>(0);
        for (String fileName : fileNames) {
            fileSizes.add(String.valueOf(Files.size(path.resolve(fileName))));
        }
    }

    public List<String> getFileSizes() {
        return fileSizes;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST;
    }
}
