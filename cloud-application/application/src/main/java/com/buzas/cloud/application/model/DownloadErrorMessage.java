package com.buzas.cloud.application.model;

public class DownloadErrorMessage extends AbstractMessage{
    @Override
    public MessageType getMessageType() {
        return MessageType.ERROR_DOWNLOAD;
    }
}
