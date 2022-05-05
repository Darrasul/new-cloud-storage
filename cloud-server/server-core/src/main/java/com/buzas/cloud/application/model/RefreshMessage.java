package com.buzas.cloud.application.model;

public class RefreshMessage extends AbstractMessage{
    @Override
    public MessageType getMessageType() {
        return MessageType.REFRESH;
    }
}
