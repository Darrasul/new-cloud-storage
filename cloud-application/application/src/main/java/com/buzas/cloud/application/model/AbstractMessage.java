package com.buzas.cloud.application.model;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    public abstract MessageType getMessageType();
}
