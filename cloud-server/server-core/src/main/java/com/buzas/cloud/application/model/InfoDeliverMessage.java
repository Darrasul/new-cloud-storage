package com.buzas.cloud.application.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InfoDeliverMessage extends AbstractMessage{

    String name;
    String fileSize;
    String fileLastUpdateFull;
    String filePath;
    Boolean fromServer = true;

    public InfoDeliverMessage(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.filePath = path.toString();
        this.fileSize = String.valueOf(Files.size(path));
        this.fileLastUpdateFull = String.valueOf(Files.getLastModifiedTime(path));
    }

    public String getName() {
        return name;
    }

    public Boolean getFromServer() {
        return fromServer;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileLastUpdateFull() {
        return fileLastUpdateFull;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.INFO_DELIVER;
    }
}
