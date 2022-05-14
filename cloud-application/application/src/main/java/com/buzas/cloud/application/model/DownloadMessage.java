package com.buzas.cloud.application.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadMessage extends AbstractMessage{

    private String name;

    public DownloadMessage(String name) throws IOException {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DOWNLOAD;
    }
}
