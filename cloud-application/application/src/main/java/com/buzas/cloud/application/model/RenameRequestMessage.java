package com.buzas.cloud.application.model;

public class RenameRequestMessage extends AbstractMessage{

    private String oldName;
    private String newName;

    public RenameRequestMessage(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RENAME_REQUEST;
    }
}
