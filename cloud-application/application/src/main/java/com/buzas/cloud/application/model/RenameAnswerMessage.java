package com.buzas.cloud.application.model;

public class RenameAnswerMessage extends AbstractMessage{

    private boolean success;

    public RenameAnswerMessage(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DIRECTORY_ANSWER;
    }
}
