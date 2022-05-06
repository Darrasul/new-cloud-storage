package com.buzas.cloud.application.model;

public class DownloadErrorMessage extends AbstractMessage{

    String message;

    public DownloadErrorMessage(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ERROR_DOWNLOAD;
    }
}
